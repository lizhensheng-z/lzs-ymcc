package cn.lzs.ymcc.api;

import cn.lzs.ymcc.dto.UpdatePayStatusOrderDTO;
import cn.lzs.ymcc.result.JSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 李振生
 */
@FeignClient(name = "service-order")
@RequestMapping("/courseOrder")
public interface OrderFeignApi {
    @PutMapping("/updatePayStatusByOrderNo")
    JSONResult updatePayStatusByOrderNo(@RequestBody UpdatePayStatusOrderDTO updatePayStatusOrderDTO);



    /**
     * 根据订单号查询购买的课程号
     */
    @GetMapping("/getCourseIdByOrderNo/{orderNo}")
    JSONResult getCourseIdByOrderNo(@PathVariable("orderNo") String orderNo);
}
