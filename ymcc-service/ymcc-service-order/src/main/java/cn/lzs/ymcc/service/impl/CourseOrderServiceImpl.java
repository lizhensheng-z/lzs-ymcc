package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.GlobalExceptionCode;
import cn.lzs.ymcc.api.CourseFeignApi;
import cn.lzs.ymcc.api.UserApi;
import cn.lzs.ymcc.constant.RedisConstants;
import cn.lzs.ymcc.constant.RocketMQConstants;
import cn.lzs.ymcc.domain.Course;
import cn.lzs.ymcc.domain.CourseOrder;
import cn.lzs.ymcc.domain.CourseOrderItem;
import cn.lzs.ymcc.domain.PayOrder;
import cn.lzs.ymcc.domain.User;
import cn.lzs.ymcc.dto.*;
import cn.lzs.ymcc.mapper.CourseOrderItemMapper;
import cn.lzs.ymcc.mapper.CourseOrderMapper;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import cn.lzs.ymcc.service.ICourseOrderItemService;
import cn.lzs.ymcc.service.ICourseOrderService;
import cn.lzs.ymcc.util.AssertUtil;
import cn.lzs.ymcc.util.CodeGenerateUtils;
import cn.lzs.ymcc.util.LoginContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
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
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    @Autowired
    private ICourseOrderItemService courseOrderItemService;
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

    /**
     * Mq监听器保存主订单和子订单
     *
     * @param courseOrder
     */
    @Override
    @Transactional // 开启事务
    public void saveOrderAndItems(CourseOrder courseOrder) {
        // 通过订单号查询订单
        CourseOrder courseOrderTmp = selectByOrderNo(courseOrder.getOrderNo());
        // 判断订单是否存在
        AssertUtil.isNull(courseOrderTmp, GlobalExceptionCode.ORDER_EXIST_ERROR.getMessage());
        // 保存订单
        insert(courseOrder);
        // 主订单获取子订单对象items  订单号有了
        List<CourseOrderItem> items = courseOrder.getItems();
        // 遍历子订单对象items
        items.forEach(item -> {
            item.setOrderId(courseOrder.getId()); // 给子订单设置id
        }); // 批量保存子订单
        courseOrderItemService.insertBatch(items);
    }

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     * @return
     */
    public CourseOrder selectByOrderNo(String orderNo) {
        Wrapper<CourseOrder> wrapper = new EntityWrapper<>();
        // 根据order_no字段查询orderNo对象
        wrapper.eq("order_no", orderNo);
        return selectOne(wrapper);
    }

    /**
     *  处理订单支付结果
     * @param orderDto
     */
    @Override
    public void payResultHandle(PyaResultDto orderDto) {
        // 查询出订单号
        CourseOrder courseOrder = selectByOrderNo(orderDto.getOrderNo());
        if (courseOrder == null){
            return; // 订单不存在,就不要推送消息了
        }
        boolean isWaitePay = courseOrder.getStatusOrder() == CourseOrder.STATE_WAITE_PAY;
        if (!isWaitePay){ // false
            return; // 订单状态不是待支付,也不要推送消息了
        }
        courseOrder.setStatusOrder(CourseOrder.STATE_PAY_SUCCESS); // 修改订单支付状态为支付成功
        courseOrder.setUpdateTime(new Date()); // 设置订单修改时间
        updateById(courseOrder); // 修改订单保存数据库
    }

    /**
     *   处理超时订单 修改订单支付状态为取消
     * @param orderNo
     * 扩展==== 可能支付宝已经支付成功,但是没有异步通知到我们 -- 主动查询支付宝
     *  只要状态是待支付,我们就可以取消订单
     *  如果后续支付宝有异步通知 支付成功, ---- 直接退款
     */
    @Override
    public void payTimeOutCancelOrder(String orderNo) {
        // 查询出订单号
        CourseOrder order = selectByOrderNo(orderNo);
        // 判断订单号是否存在, 要做业务幂等性处理,只有待支付的订单才能取消
        if (order == null){
            return; // 订单为空,就直接返回不要通知消息给我们处理了
        }
        // 判断订单支付状态是否为待支付
        boolean isWaitPay = order.getStatusOrder() == CourseOrder.STATE_WAITE_PAY;
        if (!isWaitPay){ // 如果不是待支付
            return; // 直接返回,不要在通知消息给我们处理了
        }
        // 订单存在且订单支付状态为待支付,才开始处理业务
        log.info("支付超时取消订单");
        order.setStatusOrder(CourseOrder.STATE_CANCEL); // 修改订单状态为取消
        updateById(order);
    }

    /**
     *  秒杀下单
     * @param dto
     * @return
     */
    @Override
    public String killPlaceOrder(KillOrderParamDto dto) {
        //1. 参数基本校验
        // 1.1.防重token校验
        // redis中查询预创单
        Object tmp = redisTemplate.opsForValue().get(dto.getOrderNo());
        // 校验预创单是否存在
        AssertUtil.isNotNull(tmp,GlobalExceptionCode.ORDER_IS_NULL_ERROR.getMessage());
        // 将预付单强转为PreOrderDto对象
        PreOrderDto orderDto = (PreOrderDto)tmp;
        // 设置登录Id,为预创单中的登录人Id
        Long loginId = orderDto.getUserId();
        // 拼接Key从redis获取token
        String key = "token:" + loginId + ":" + orderDto.getCourseId();
        // 从redis中查询token,课程id
        Object tokenTmp = redisTemplate.opsForValue().get(key);
        // 校验token是否存在
        AssertUtil.isNotNull(tokenTmp, GlobalExceptionCode.TOKEN_IS_NULL_ERROR.getMessage());
        // 校验token是否正确
        AssertUtil.isEquals(dto.getToken(), tokenTmp.toString(), GlobalExceptionCode.TOKEN_IS_NULL_ERROR.getMessage());

        // 2.查询多个课程 + 课程销售
        // 为课程服务编写controller接口 - info [课程订单数据]
        // 为课程服务编写api-course
        // 2.1.通过feign调用课程服务,获取课程 + 销售信息
        JSONResult jsonResult = courseFeignApi.info(orderDto.getCourseId().toString()); // 使用courseFeign调用info查询订单信息
        // 校验课程数据是否正确
        AssertUtil.isTrue(jsonResult.isSuccess(), GlobalExceptionCode.ORDER_INFO_ERROR.getMessage());
        // 校验课程是否存在
        AssertUtil.isNotNull(jsonResult.getData(), GlobalExceptionCode.SERVICE_PARAM_IS_NULL.getMessage());
        // 转换对象为订单数据对象
        List<CourseDTO> courseDto = JSON.parseArray(jsonResult.getData().toString(), CourseDTO.class);


        //  获取到课程详细对象
        for (CourseDTO courseDto1 : courseDto) {
            //3.保存订单以及订单明细
            // 3.1.创建主订单对象,使用主订单保存订单数据
            CourseOrder order = new CourseOrder();
            Date date = new Date();
            order.setCreateTime(date); // 设置创建时间
            order.setOrderNo(orderDto.getOrderNo());  // 设置订单号为预创单的订单号,
            order.setTotalCount(1); // 设置秒杀数量
            order.setStatusOrder(CourseOrder.STATE_WAITE_PAY); // 设置订单状态
            order.setUserId(orderDto.getUserId()); // 设置用户id为预测单中的用户Id
            order.setPayType(dto.getPayType()); // 设置支付方式
            order.setTotalAmount(orderDto.getTotalAmount()); // 设置金额为预创订单对象的金额
            // 3.2.保存主订单
            // insert(order);  交给Mq的事务监听器去保存本地事务

            // 4.创建子订单
            // 4.1.创建一个StringBuffer类型的对象title 用于添加订单标题
            StringBuffer title = new StringBuffer();
            // 4.2.使用title对象调用append的方法: title对象的末尾字符串第一次添加为: 课程[ 字符串
            title.append("购买课程[ ");
            // 获取到course课程对象和courseMarket课程销售对象
            Course course = courseDto1.getCourse();

            // 4.4.创建一个课程订单明细对象item
            CourseOrderItem item = new CourseOrderItem();
            item.setOrderId(order.getId()); // 设置订单Id
            item.setAmount(orderDto.getTotalAmount()); // 设置子订单金额为预订单对象中的金额
            item.setCount(1); // 设置秒杀课程的数量
            item.setCreateTime(date); // 设置订单的创建时间
            item.setCourseId(course.getId()); // 设置秒杀课程ID
            item.setCourseName(course.getName()); // 设置秒杀课程的名称
            item.setCoursePic(course.getPic()); // 设置秒杀课程的封面
            item.setOrderNo(order.getOrderNo()); // 设置订单号

            // 4.5.保存数据
            //   courseOrderItemService.insert(item);  保存子订单 交给Mq的事务监听器去保存本地事务

            // 主订单获取到子订单,在添加设置过值的子订单对象item进去
            order.getItems().add(item);
            title.append(course.getName()); // title对象末尾字符串第二次添加为: 课程[ 课程名称

            title.append("]订单"); // title对象末尾字符串第三次添加为: 课程[ 课程名称]订单
            order.setTitle(title.toString()); // 设置标题,将title对象转为字符串

            //  updateById(order); // 修改订单 交给Mq的事务监听器去保存本地事务

            // 主订单和子订单都有完整信息了
            /*
             message(消息体): 用来生成支付单的参数 订单号 支付金额  支付方式 标题 userId  扩展参数
             arg(扩展参数): 使用扩展参数,传递主订单和子订单,方便执行本地事务
             */
            // =========== 支付相关 ===========  发送事务消息,让支付服务消费消息保存支付单
            // new一个Map,将用户id和多个课程id添加进map, 作为支付单表中的扩展参数传入所需要的值,
            HashMap<String, Object> map = new HashMap<>();
            map.put("loginId",loginId);
            map.put("courseIds",orderDto.getUserId());

            // 创建消息体的dto,将需要的参数传入构造器
            Order2payOrderParamDto paramDto = new Order2payOrderParamDto(
                    order.getTotalAmount(),
                    dto.getPayType(),
                    order.getOrderNo(),
                    loginId,
                    JSON.toJSONString(map), // 扩展参数  这里的参数是支付单表中扩展参数所需要的数据, 后面发送事务消息需要的Dto里面的值
                    order.getTitle()
            );
            // 先将paramDto对象转成一个JSON格式的字符串
            String jsonString = JSON.toJSONString(paramDto);
            // 在调用MessageBuilder的withPayload方法,将JSON格式的对象jsonString传入调用build方法,创建为Message类型对象
            Message<String> message = MessageBuilder.withPayload(jsonString).build();

            // 发送事务消息方法: sendMessageInTransaction()  参数一: 发送事务消息组名 [事务消息监听这个组名]  参数二:标题  参数三:消息体  参数四: 扩展参数
            TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction(
                    "TxPayOrderGroupListener",
                    "topic-order:tag-order",
                    message,  // 消息体 [对象]
                    order);// 扩展参数

            // 通过发送事务消息对象,获取到本地事务状态对象
            LocalTransactionState localTransactionState = transactionSendResult.getLocalTransactionState(); // 本地事务执行状态
            // 通过发送事务消息对象,获取消息发送状态对象
            SendStatus status = transactionSendResult.getSendStatus();
            // 判断事务执行状态和消息发送状态是否成功
            boolean isError = localTransactionState != LocalTransactionState.COMMIT_MESSAGE || status != SendStatus.SEND_OK;
            AssertUtil.isFalse(isError, GlobalExceptionCode.ORDER_FAILED_ERROR.getMessage());

            try {
                // 支付超时取消,延迟消息
                // 参数一: 发送消息组名   参数二: 消息体 (订单号)   参数三: 超时时间(毫秒)  参数四: 延迟等级
                SendResult sendResult = rocketMQTemplate.syncSend(
                        "topic-paytimeout:tag-paytimeout",
                        MessageBuilder.withPayload(order.getOrderNo()).build(),
                        3000,
                        5
                );
                // 判断发送状态是否成功
                boolean isDelayOk = sendResult.getSendStatus() == SendStatus.SEND_OK;
                if (!isDelayOk){ // 判断发送延迟信息是否成功
                    // 兜底: 1.重试发送  2.记录数据库日志  3.发各种通知短信到代码负责人,运维....人工介入处理
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 兜底: 1.重试发送  2.记录数据库日志  3.发各种通知短信到代码负责人,运维....人工介入处理
            }

            // 最后业务完成 删除redis中的防重token
            redisTemplate.delete(key);

            // 删除预创单
            redisTemplate.delete(dto.getOrderNo());

            // 返回订单编号
            return order.getOrderNo();
        }

        return GlobalExceptionCode.ORDER_FAILED_ERROR.getMessage();

    }

}
