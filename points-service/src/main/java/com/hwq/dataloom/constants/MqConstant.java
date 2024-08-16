package com.hwq.dataloom.constants;

/**
 * @author HWQ
 * @date 2024/8/13 08:54
 * @description
 */
public interface MqConstant {
    /**
     * 延迟交换机
     */
    String ORDER_DELAYED_EXCHANGE = "order_delayed_exchange";

    /**
     * 延迟队列
     */
    String ORDER_DELAYED_QUEUE = "order_delayed_queue";

    /**
     * 延迟队列路由
     */
    String ORDER_DELAYED_ROUTING_KEY = "order_delayed_routingKey";

    /**
     * 死信交换机
     */
    String ORDER_DEAD_EXCHANGE_NAME = "order_dead_exchange";

    /**
     * 死信队列
     */
    String ORDER_DEAD_QUEUE_NAME = "order_dead_queue";

    /**
     * 死信路由键
     */
    String ORDER_DEAD_ROUTING_KEY = "order_dead_route";
}
