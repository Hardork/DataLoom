package com.hwq.dataloom.config;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HWQ
 * @date 2024/8/25 00:09
 * @description 公共线程池
 */
@Component
@Data
public class CommonThreadPool {

    private int corePoolSize = 10;

    private int maximumPoolSize = 10;

    /**
     * 队列最大任务堆积上限
     */
    private int maxQueueSize = 10;

    private int keepAliveTime = 600;

    private int capacity = 1000;

    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void init() {
        this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(capacity));
    }

    @PreDestroy
    public void shutdown() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
        }
    }

    /**
     * 线程池是否可用(实际队列数是否小于最大队列数)
     *
     * @return true为可用，false不可用
     */
    public boolean available() {
        return threadPoolExecutor.getQueue().size() <= maxQueueSize;
    }

    /**
     * 添加任务，不强制限制队列数
     * @param task 任务
     */
    public void addTask(Runnable task) {
        threadPoolExecutor.execute(task);
    }



}
