package com.hwq.dataloom.mq.init;

import com.hwq.dataloom.constants.MqConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
public class OrderInitMain {
    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("127.0.0.1");
            factory.setUsername("admin");
            factory.setPassword("123");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            // 创建延迟交换机
            channel.exchangeDeclare(MqConstant.ORDER_DELAYED_EXCHANGE, "direct", true, false, null);
            // 创建延迟队列
            Map<String, Object> delayQueueArgs = new HashMap<>();
            // 设置死信去往的交换机
            delayQueueArgs.put("x-dead-letter-exchange", MqConstant.ORDER_DEAD_EXCHANGE_NAME);
            delayQueueArgs.put("x-dead-letter-routing-key", MqConstant.ORDER_DEAD_ROUTING_KEY);
            // 设置延迟队列的ttl 也就是延迟消息的时间 5min
            delayQueueArgs.put("x-message-ttl", 5*60*1000);
            channel.queueDeclare(MqConstant.ORDER_DELAYED_QUEUE, true, false, false, delayQueueArgs);
            channel.queueBind(MqConstant.ORDER_DELAYED_QUEUE, MqConstant.ORDER_DELAYED_EXCHANGE, MqConstant.ORDER_DELAYED_ROUTING_KEY);

            // 创建死信交换机
            channel.exchangeDeclare(MqConstant.ORDER_DEAD_EXCHANGE_NAME, "direct",true, false, false, null);
            // 创建死信队列
            channel.queueDeclare(MqConstant.ORDER_DEAD_QUEUE_NAME, true, false,false, null);
            channel.queueBind(MqConstant.ORDER_DEAD_QUEUE_NAME, MqConstant.ORDER_DEAD_EXCHANGE_NAME, MqConstant.ORDER_DEAD_ROUTING_KEY);
            System.out.println("创建完毕");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
