package com.hwq.bi.bizmq;

public interface BiMqConstant {

    /**
     * 普通交换机名称
     */
    String BI_EXCHANGE_NAME = "bi_exchange";

    /**
     * 普通队列名称
     */
    String BI_QUEUE_NAME = "bi_queue";

    /**
     * 普通路由名称
     */
    String BI_ROUTING_KEY = "bi_routingKey";

    /**
     * 死信交换机名称
     */
    String BI_DEAD_EXCHANGE_NAME = "bi_dead_exchange";

    /**
     * 死信队列名称
     */
    String BI_DEAD_QUEUE_NAME = "bi_dead_queue";

    /**
     * 死信路由名称
     */
    String DEAD_ROUTING_KEY = "bi_dead_route";

    //
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

    String ORDER_DEAD_EXCHANGE_NAME = "order_dead_exchange";

    String ORDER_DEAD_QUEUE_NAME = "order_dead_queue";

    String ORDER_DEAD_ROUTING_KEY = "order_dead_route";




}
