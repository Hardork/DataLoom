package com.hwq.dataloom;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author HWQ
 * @date 2024/8/12 23:32
 * @description
 */
@SpringBootApplication
@MapperScan("com.hwq.dataloom.mapper")
@EnableScheduling
@EnableDubbo
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class PointsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PointsApplication.class, args);
    }

}