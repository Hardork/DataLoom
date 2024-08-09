package com.hwq.dataloom.bizmq.test;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author HWQ
 * @date 2024/4/29 11:05
 * @description
 */
public class Producer {
    //生产者
    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        // 设置服务端的地址、端口、用户名和密码...
        factory.setHost("127.0.0.1");
        factory.setUsername("admin");
        factory.setPassword("123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("Queue_Java", false, false, false, null);

        for(int i = 0; i < 20; i++) {
            byte[] message = ("message" + i).getBytes();
            channel.basicPublish("", "Queue_Java", null, message);
        }

        channel.close();
        connection.close();
    }
}
