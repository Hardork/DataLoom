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
}
