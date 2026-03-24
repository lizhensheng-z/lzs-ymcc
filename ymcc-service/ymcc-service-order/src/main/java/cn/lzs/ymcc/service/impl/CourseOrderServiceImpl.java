package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.api.CourseFeignApi;
import cn.lzs.ymcc.api.UserApi;
import cn.lzs.ymcc.constant.RedisConstants;
import cn.lzs.ymcc.constant.RocketMQConstants;
import cn.lzs.ymcc.domain.Course;
import cn.lzs.ymcc.domain.CourseOrder;
import cn.lzs.ymcc.domain.CourseOrderItem;
import cn.lzs.ymcc.domain.PayOrder;
import cn.lzs.ymcc.domain.User;
import cn.lzs.ymcc.dto.CourseInfoDTO;
import cn.lzs.ymcc.dto.CourseOrderDTO;
import cn.lzs.ymcc.dto.PlaceOrderDTO;
import cn.lzs.ymcc.dto.UpdatePayStatusOrderDTO;
import cn.lzs.ymcc.mapper.CourseOrderItemMapper;
import cn.lzs.ymcc.mapper.CourseOrderMapper;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import cn.lzs.ymcc.service.ICourseOrderService;
import cn.lzs.ymcc.util.CodeGenerateUtils;
import cn.lzs.ymcc.util.LoginContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-09-19
 */
@Service
public class CourseOrderServiceImpl extends ServiceImpl<CourseOrderMapper, CourseOrder> implements ICourseOrderService {


    private static final Logger log = LoggerFactory.getLogger(CourseOrderServiceImpl.class);
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @Autowired
    private CourseOrderMapper courseOrderMapper;
    @Autowired
    private CourseFeignApi courseFeignApi;
    @Autowired
    private UserApi userApi;
    @Autowired
    private CourseOrderItemMapper courseOrderItemMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 生成订单相关信息
     * @param placeOrderDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String placeOrder(PlaceOrderDTO placeOrderDTO) {
        //1 参数校验
        List<Long> courseIds = placeOrderDTO.getCourseIds();
        if (courseIds == null || courseIds.size() == 0) {
            throw new GlobalBusinessException("课程id不能为空");
        }
        Integer payType = placeOrderDTO.getPayType();
        if (payType == null) {
            throw new GlobalBusinessException("支付类型不能为空");
        }
        //TODO 可优化
        if (payType != 1 && payType != 2 && payType != 3 &&payType != 0) {
            throw new GlobalBusinessException("支付类型不合法");
        }
        //校验token
        String token = placeOrderDTO.getToken();
        if (token == null) {
            throw new GlobalBusinessException("token不能为空");
        }
        //拼凑rediskey TODO 业务key中的courseId只有一个，有些缺陷
        Long loginId = LoginContext.getLogin().getId();
        Long courseId = courseIds.get(0);
        String key = String.format(RedisConstants.REDIS_REPEAT_REFRESH_KEY_PREFIX, loginId, courseId);
        Object orderToken = redisTemplate.opsForValue().get(key);
        if (orderToken == null) {
            throw new GlobalBusinessException("token已失效,请勿重复提交订单");
        }
        if (!orderToken.toString().equals(token)) {
            throw new GlobalBusinessException("token不合法,请勿重复提交订单");
        }
        //2 生成订单信息
        //t_course_order:远程调用course-service
        JSONResult courseInfo = courseFeignApi.getCourseInfo(courseIds);
        CourseOrderDTO courseOrderDTO = createCourseOrders(courseInfo, loginId, payType);
        //生成支付信息
        PayOrder payOrder = createPayOrder(courseOrderDTO, payType);

        //发送事务消息：
        //事务监听器：本地事务:订单，订单详情落库
        //消息内容到底应该长成什么样子？
        TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction(
                //事务监听器组名字
                RocketMQConstants.MQ_COURSEORDER_PAY_GROUP_TRANSACTION,
                //主题：标签
                RocketMQConstants.MQ_TOPIC_ORDER + ":" + RocketMQConstants.MQ_TAGS_COURSEORDER_PAYORDER,
                //消息：用作保存支付单
                MessageBuilder.withPayload(JSONObject.toJSONString(payOrder)).build(),
                //参数：用作保存课程订单和明细
                courseOrderDTO);
        //检验事务消息是否发送成功
        if (transactionSendResult.getLocalTransactionState() != LocalTransactionState.COMMIT_MESSAGE) {
            throw new GlobalBusinessException("支付订单生成失败，请稍后重试");
        }
        //发送延时消息
        SendResult delayMessageSendResult = rocketMQTemplate.syncSend(RocketMQConstants.MQ_TOPIC_COURSEORDER_CANCEL_DELAY + ":" + RocketMQConstants.MQ_TAGS_COURSEORDER_CANCEL_DELAY,
                MessageBuilder.withPayload(payOrder.getOrderNo()).build(),
                3000, 3);
        log.info("延时消息发送结果：{}", delayMessageSendResult.getSendStatus());
        if(!delayMessageSendResult.getSendStatus().equals(SendStatus.SEND_OK)){
            //TODO 进行重试
            throw new GlobalBusinessException("延时消息发送失败，请稍后重试");
        }
        //订单生成成功，删除redis中的token
        redisTemplate.delete(key);
        return courseOrderDTO.getCourseOrder().getOrderNo();
    }

    private static PayOrder createPayOrder(CourseOrderDTO courseOrderDTO, Integer payType) {
        PayOrder payOrder = new PayOrder();
        payOrder.setOrderNo(courseOrderDTO.getCourseOrder().getOrderNo());
        payOrder.setAmount(courseOrderDTO.getCourseOrder().getTotalAmount());
        payOrder.setPayType(payType);
        payOrder.setPayStatus(PayOrder.PendingPayment);
        payOrder.setCreateTime(new Date());
        payOrder.setUpdateTime(new Date());
        payOrder.setSubject(courseOrderDTO.getCourseOrder().getTitle());
        payOrder.setUserId(courseOrderDTO.getCourseOrder().getUserId());
        return payOrder;
    }

    private CourseOrderDTO createCourseOrders(JSONResult courseInfo, Long loginId, Integer payType) {
        //转object，转json
        Object data = courseInfo.getData();
        String jsonString = JSONObject.toJSONString(data);
        CourseInfoDTO courseInfoDTO = JSONObject.parseObject(jsonString, CourseInfoDTO.class);
        //3 保存订单信息
        CourseOrder courseOrder = new CourseOrder();
        //根据loginId 查询  userId
        JSONResult userByLoginId = userApi.getUserByLoginId(loginId);
        Object data1 = userByLoginId.getData();
        String jsonString1 = JSONObject.toJSONString(data1);
        User user = JSONObject.parseObject(jsonString1, User.class);
        courseOrder.setUserId(user.getId());
        courseOrder.setCreateTime(new Date());
        courseOrder.setUpdateTime(new Date());
        String orderNo = CodeGenerateUtils.generateOrderSn(user.getId());
        courseOrder.setOrderNo(orderNo);
        courseOrder.setTotalAmount(courseInfoDTO.getTotalAmount());
        courseOrder.setPayType(payType);
        courseOrder.setTotalCount(courseInfoDTO.getCourses().size());
        courseOrder.setStatusOrder(CourseOrder.PendingPayment);//下单成功待支付
        //获取订单标题
        String name = courseInfoDTO.getCourses().get(0).getCourse().getName();
        courseOrder.setTitle(name);
        courseOrder.setVersion(0);
        //courseOrderMapper.insert(courseOrder);
        //TODO 订单明细表
        List<CourseOrderItem> courseOrderItems = new ArrayList<>();
        for (int i = 0; i < courseInfoDTO.getCourses().size(); i++) {
            CourseOrderItem courseOrderItem = new CourseOrderItem();
            courseOrderItem.setCourseId(courseInfoDTO.getCourses().get(i).getCourse().getId());
            courseOrderItem.setCourseName(courseInfoDTO.getCourses().get(i).getCourse().getName());
            courseOrderItem.setCoursePic(courseInfoDTO.getCourses().get(i).getCourse().getPic());
            courseOrderItem.setOrderNo(orderNo);
            courseOrderItem.setAmount(courseInfoDTO.getCourses().get(i).getCourseMarket().getPrice());
            courseOrderItem.setVersion(0);
            courseOrderItem.setCourseId(courseInfoDTO.getCourses().get(i).getCourse().getId());
            courseOrderItem.setCount(CourseOrderItem.OneCount);
            courseOrderItem.setCreateTime(new Date());
            courseOrderItem.setUpdateTime(new Date());
            courseOrderItems.add(courseOrderItem);
        }
        //courseOrderItemMapper.insertBatch(courseOrderItems);
        CourseOrderDTO courseOrderDTO = new CourseOrderDTO();
        courseOrderDTO.setCourseOrder(courseOrder);
        courseOrderDTO.setCourseOrderItemList(courseOrderItems);
        return courseOrderDTO;
    }

    /**
     * 根据订单号修改订单状态
     * @param
     * @return
     */
    @Override
    public Boolean updatePayStatusByOrderNo(UpdatePayStatusOrderDTO updatePayStatusOrderDTO) {
        return courseOrderMapper.updatePayStatusByOrderNo(updatePayStatusOrderDTO);
    }

    /**
     * 根据订单号查询已购买的课程id
     * @param orderNo
     * @return
     */
    @Override
    public List<Long> getCourseIdByOrderNo(String orderNo) {
       List<Long> courseId = courseOrderItemMapper.getCourseIdByOrderNo(orderNo);
       return courseId;
    }
}
