package com.hwq.dataloom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author HWQ
 * @date 2024/8/31 21:44
 * @description
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    /**
     * 用户信息传输拦截器
     */
    @Bean
    public UserInterceptor userTransmitInterceptor() {
        return new UserInterceptor();
    }

    /**
     * 添加用户信息传递过滤器至相关路径拦截
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTransmitInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/doc.html/**")
        ;
    }
}
