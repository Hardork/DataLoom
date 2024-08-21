package com.hwq.dataloom.service.basic;

import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/8/20 16:12
 * @description 数据源策略选择器
 */
@Component
public class DatasourceStrategyChoose implements ApplicationContextAware, CommandLineRunner {

    /**
     * 应用上下文，通过Spring IOC 获取Bean
     */
    private ApplicationContext applicationContext;

    /**
     * 执行策略集合
     */
    private final Map<String, DatasourceExecuteStrategy> executeStrategyMap = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
        // 从IOC容器中获取策略类
        Map<String, DatasourceExecuteStrategy> datasourceExecuteStrategies = applicationContext.getBeansOfType(DatasourceExecuteStrategy.class);
        datasourceExecuteStrategies.forEach((beanName, bean) -> {
            DatasourceExecuteStrategy exist = executeStrategyMap.get(beanName);
            if (exist != null) {
                throw new RuntimeException("有相同的策略实现类");
            }
            executeStrategyMap.put(bean.mark(), bean);
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据 mark 查询具体策略
     * @param mark 策略标识
     * @return 实际执行策略
     */
    public DatasourceExecuteStrategy choose(String mark) {
        return executeStrategyMap.get(mark);
    }
}
