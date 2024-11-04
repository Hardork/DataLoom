package com.hwq.dataloom.service.basic.handler;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * @author HWQ
 * @date 2024/8/27 00:59
 * @description 责任链处理器容器
 */
@Component
@Slf4j
public class AITaskChainContext<T> implements ApplicationContextAware, CommandLineRunner {

    /**
     * 使用一个Map存储策略组
     * <策略组标识，处理器集合>
     */
    private final Map<String, List<AITaskAbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();

    /**
     * 应用上下文，用于获取Spring IOC 的 Bean 实例
     */
    private ApplicationContext applicationContext;


    /**
     * 根据mask找到对应的策略组，进行处理
     * @param mark
     * @param request
     */
    public void handle(String mark, T request) {
        List<AITaskAbstractChainHandler> chainHandlers = abstractChainHandlerContainer.get(mark);
        if (CollectionUtil.isEmpty(chainHandlers)) {
            throw new RuntimeException(String.format("无对应策略组{%s}", mark));
        }
        chainHandlers.forEach(each -> each.handle(request));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, AITaskAbstractChainHandler> handlerMap = applicationContext.getBeansOfType(AITaskAbstractChainHandler.class);
        handlerMap.forEach((beanName, bean) -> {
            // 获取处理器对应的集合
            List<AITaskAbstractChainHandler> list = abstractChainHandlerContainer.getOrDefault(bean.mark(), new ArrayList<>());
            // 将处理器加入对应策略组
            list.add(bean);
            // 更新策略容器
            abstractChainHandlerContainer.put(bean.mark(), list);
        });
        // 遍历策略组，根据order进行排序，决定执行顺序
        abstractChainHandlerContainer.forEach((mark, list) -> list.sort(Comparator.comparing(Ordered::getOrder)));
    }
}
