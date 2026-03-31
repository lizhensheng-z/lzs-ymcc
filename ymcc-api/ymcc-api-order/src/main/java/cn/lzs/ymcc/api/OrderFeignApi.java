package cn.lzs.ymcc.api;

import cn.lzs.ymcc.dto.UpdatePayStatusOrderDTO;
import cn.lzs.ymcc.result.JSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    /**
     * 保存秒杀订单（简化版，直接标记为已支付）
     * @param orderNo 订单号
     * @param courseId 课程ID
     * @param amount 金额
     * @param userId 用户ID
     * @param payType 支付方式
     * @return 订单号
     */
    @PostMapping("/saveKillOrder")
    JSONResult saveKillOrder(@RequestParam("orderNo") String orderNo,
                             @RequestParam("courseId") Long courseId,
                             @RequestParam("amount") BigDecimal amount,
                             @RequestParam("userId") Long userId,
                             @RequestParam("payType") Integer payType);
}
