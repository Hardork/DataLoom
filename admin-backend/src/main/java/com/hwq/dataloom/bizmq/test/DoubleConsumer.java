package com.hwq.dataloom.bizmq.test;

import com.rabbitmq.client.*;

/**
 * @author HWQ
 * @date 2024/4/29 11:03
 * @description
 */
public class DoubleConsumer {


    //消费者
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        // 设置服务端的地址、端口、用户名和密码...
        factory.setHost("127.0.0.1");
        factory.setUsername("admin");
        factory.setPassword("123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("Queue_Java", false, false, false, null);
        // 设置预取数量为1
        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body);
                System.out.println("Received: " + message);
                try {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // 标识进程，第二个消费者将输出内容改为“Consumer2:”，再次运行程序即可
        System.out.println("Consumer2:");
        channel.basicConsume("Queue_Java", false, consumer);
    }


}

