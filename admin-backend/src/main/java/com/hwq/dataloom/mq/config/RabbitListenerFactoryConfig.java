package com.hwq.dataloom.mq.config;

import lombok.Data;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HWQ
 * @date 2024/5/18 19:42
 * @description 消费者工厂配置类
 */
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Data
public class RabbitListenerFactoryConfig {
    private Integer initConsumer;
    private Integer maxConsumer;

    /**
     * kimi消费者工厂(最大支持128k上下文) 主要分析大文本数据（VIP专属）
     * @param connectionFactory
     * @return
     */
    @Bean
    public SimpleRabbitListenerContainerFactory kimiContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(initConsumer); // 设置初始消费者
        factory.setMaxConcurrentConsumers(maxConsumer); // 设置最大消费者
        factory.setPrefetchCount(1); // 消费者每次拉取的消息数量
        return factory;
    }

    /**
     * gpt消费者工厂 （最大支持8K的上下文） 主要分析小文本数据（普通用户）
     * @param connectionFactory
     * @return
     */
    @Bean
    public SimpleRabbitListenerContainerFactory gptContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(initConsumer); // 设置初始消费者
        factory.setMaxConcurrentConsumers(maxConsumer); // 设置最大消费者
        factory.setPrefetchCount(1); // 消费者每次拉取的消息数量
        return factory;
    }
}
