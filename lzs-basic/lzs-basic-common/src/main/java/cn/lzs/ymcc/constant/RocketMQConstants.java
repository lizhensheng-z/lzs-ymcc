package cn.lzs.ymcc.constant;

/**
 * @author 李振生
 */
public interface RocketMQConstants {
    // 事务消息-订单支付群组
  String  MQ_COURSEORDER_PAY_GROUP_TRANSACTION = "mq_courseorder_pay_group_transaction";
    //消息主题order
   String MQ_TOPIC_ORDER = "mq_topic_order";
   //消息标签：支付下的课程支付
   String MQ_TAGS_COURSEORDER_PAYORDER = "mq_tags_courseorder_payorder";

   //延迟消息

   //延迟消息-取消订单主题
   String MQ_TOPIC_COURSEORDER_CANCEL_DELAY = "mq_topic_courseorder_cancel_delay";
   //延迟消息-取消订单标签
   String MQ_TAGS_COURSEORDER_CANCEL_DELAY = "mq_tags_courseorder_cancel_delay";
}
