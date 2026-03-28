package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.dto.KillOrderParamDto;
import cn.lzs.ymcc.dto.PlaceOrderDTO;
import cn.lzs.ymcc.dto.UpdatePayStatusOrderDTO;
import cn.lzs.ymcc.service.ICourseOrderService;
import cn.lzs.ymcc.domain.CourseOrder;
import cn.lzs.ymcc.query.CourseOrderQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/courseOrder")
public class CourseOrderController {

    @Autowired
    public ICourseOrderService courseOrderService;
    /**
     *  秒杀下单
     * @param dto
     * @return
     */
    @PostMapping("/killPlaceOrder")
    public JSONResult killPlaceOrder(@Valid @RequestBody KillOrderParamDto dto){
        String orderNo = courseOrderService.killPlaceOrder(dto);
        return JSONResult.success(orderNo);
    }
    /**
     * 生成支付订单
     */
    @RequestMapping(value="/placeOrder",method= RequestMethod.POST)
    public JSONResult placeOrder(@RequestBody PlaceOrderDTO placeOrderDTO){
        //返回订单编号
        String orderNo = courseOrderService.placeOrder(placeOrderDTO);
        return JSONResult.success(orderNo);
    }

    /**
     * 根据订单号修改支付状态
     * @param updatePayStatusOrderDTO
     * @return
     */
    @PutMapping("/updatePayStatusByOrderNo")
    public JSONResult updatePayStatusByOrderNo(@RequestBody UpdatePayStatusOrderDTO updatePayStatusOrderDTO){
      //TODO 修改订单状态，订单号，修改时间封装为DTO
        return JSONResult.success( courseOrderService.updatePayStatusByOrderNo(updatePayStatusOrderDTO));
    }

    /**
     * 根据订单号查询购买的课程号
     */
    @GetMapping("/getCourseIdByOrderNo/{orderNo}")
    public JSONResult getCourseIdByOrderNo(@PathVariable("orderNo") String orderNo){
        List<Long> courseIds = courseOrderService.getCourseIdByOrderNo(orderNo);
        return JSONResult.success(courseIds);
    }
    /**
    * 保存和修改公用的
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public JSONResult saveOrUpdate(@RequestBody CourseOrder courseOrder){
        if(courseOrder.getId()!=null){
            courseOrderService.updateById(courseOrder);
        }else{
            courseOrderService.insert(courseOrder);
        }
        return JSONResult.success();
    }

    /**
    * 删除对象
    */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id){
        courseOrderService.deleteById(id);
        return JSONResult.success();
    }

    /**
   * 获取对象
   */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id")Long id){
        return JSONResult.success(courseOrderService.selectById(id));
    }


    /**
    * 查询所有对象
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JSONResult list(){
        return JSONResult.success(courseOrderService.selectList(null));
    }


    /**
    * 带条件分页查询数据
    */
    @RequestMapping(value = "/pagelist",method = RequestMethod.POST)
    public JSONResult page(@RequestBody CourseOrderQuery query){
        Page<CourseOrder> page = new Page<CourseOrder>(query.getPage(),query.getRows());
        page = courseOrderService.selectPage(page);
        return JSONResult.success(new PageList<CourseOrder>(page.getTotal(),page.getRecords()));
    }
}
