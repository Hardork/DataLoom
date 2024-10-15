package com.hwq.dataloom.account;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
@SpringBootApplication
@MapperScan("com.hwq.dataloom.account.mapper")
@EnableScheduling
@EnableDubbo
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class AccountApplication {
}
