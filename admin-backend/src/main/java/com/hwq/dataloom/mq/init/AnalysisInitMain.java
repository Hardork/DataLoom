package com.hwq.dataloom.mq.init;

import com.hwq.dataloom.mq.constant.AnalysisMqConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
public class AnalysisInitMain {

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("127.0.0.1");
            factory.setUsername("admin");
            factory.setPassword("123");
            factory.setConnectionTimeout(10000); // 设置连接超时时间为 10 秒
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            // 创建普通交换机
            String EXCHANGE_NAME =  AnalysisMqConstant.BI_EXCHANGE_NAME;
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, false, null);

            // 创建队列，分配一个队列名称
            String queueName = AnalysisMqConstant.GEN_CHART_NAME;
            // 设定参数（发送死信传到哪个交换机）
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-dead-letter-exchange", AnalysisMqConstant.BI_DEAD_EXCHANGE_NAME);
            arguments.put("x-dead-letter-routing-key", AnalysisMqConstant.DEAD_ROUTING_KEY);
            // 设置延迟队列的ttl 也就是延迟消息的时间 10min
            arguments.put("x-message-ttl", 10 * 60 * 1000);
            // 声明普通队列
            channel.queueDeclare(queueName, true, false, false, arguments);
            channel.queueBind(queueName, EXCHANGE_NAME,  AnalysisMqConstant.BI_ROUTING_KEY);


            // 创建VIP交换机
            String VIP_EXCHANGE_NAME =  AnalysisMqConstant.BI_VIP_EXCHANGE_NAME;
            channel.exchangeDeclare(VIP_EXCHANGE_NAME, "direct", true, false, false, null);

            // 创建队列，分配一个队列名称
            String vipQueueName = AnalysisMqConstant.GEN_VIP_CHART_NAME;

            // 声明VIP队列
            channel.queueDeclare(vipQueueName, true, false, false, null);
            channel.queueBind(vipQueueName, VIP_EXCHANGE_NAME,  AnalysisMqConstant.BI_VIP_ROUTING_KEY);


            // 创建死信交换机
            String DEAD_EXCHANGE_NAME = AnalysisMqConstant.BI_DEAD_EXCHANGE_NAME;
            channel.exchangeDeclare(DEAD_EXCHANGE_NAME, "direct", true, false,false, null);

            // 创建死信队列
            String deadQueueName = AnalysisMqConstant.BI_DEAD_QUEUE_NAME;
            channel.queueDeclare(deadQueueName, true, false,false, null);
            channel.queueBind(deadQueueName, DEAD_EXCHANGE_NAME, AnalysisMqConstant.DEAD_ROUTING_KEY);
            System.out.println("创建完毕");
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
