package com.hwq.dataloom;
import com.hwq.dataloom.framework.service.InnerHealthCheckService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author HWQ
 * @date 2024/8/14 14:54
 * @description
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
@EnableDubbo
@Service
public class DataLoomGatewayApplication {

    @DubboReference
    private InnerHealthCheckService innerHealthCheckService;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DataLoomGatewayApplication.class, args);
        DataLoomGatewayApplication application = context.getBean(DataLoomGatewayApplication.class);
        application.printHealthCheckRes();
    }


    public void printHealthCheckRes() {
        System.out.println(innerHealthCheckService.checkHealth());
    }
}
