package com.hwq.bi.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
public class BiInitMain {

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.200.131");
            factory.setUsername("admin");
            factory.setPassword("123");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            // 创建普通交换机
            String EXCHANGE_NAME =  BiMqConstant.BI_EXCHANGE_NAME;
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, false, null);

            // 创建队列，分配一个队列名称
            String queueName = BiMqConstant.BI_QUEUE_NAME;
            // 设定参数（发送死信传到哪个交换机）
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-dead-letter-exchange", BiMqConstant.BI_DEAD_EXCHANGE_NAME);
            arguments.put("x-dead-letter-routing-key", BiMqConstant.DEAD_ROUTING_KEY);
            // 声明普通队列
            channel.queueDeclare(queueName, true, false, false, arguments);
            channel.queueBind(queueName, EXCHANGE_NAME,  BiMqConstant.BI_ROUTING_KEY);

            // 创建死信交换机
            String DEAD_EXCHANGE_NAME = BiMqConstant.BI_DEAD_EXCHANGE_NAME;
            channel.exchangeDeclare(DEAD_EXCHANGE_NAME, "direct", true, false,false, null);

            // 创建死信队列
            String deadQueueName = BiMqConstant.BI_DEAD_QUEUE_NAME;
            channel.queueDeclare(deadQueueName, true, false,false, null);
            channel.queueBind(deadQueueName, DEAD_EXCHANGE_NAME, BiMqConstant.DEAD_ROUTING_KEY);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
