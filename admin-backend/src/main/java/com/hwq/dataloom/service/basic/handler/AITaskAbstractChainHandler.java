package com.hwq.dataloom.service.basic.handler;

import org.springframework.core.Ordered;

/**
 * @author HWQ
 * @date 2024/8/27 01:01
 * @description 责任链处理器接口
 */
public interface AITaskAbstractChainHandler<T> extends Ordered {

    /**
     * @return 处理器唯一标识
     */
    String mark();

    /**
     * 执行责任链逻辑
     * @param request 处理参数
     */
    void handle(T request);

    /**
     * 失败后执行逻辑
     */
    void doAfterFailed(T request);

    /**
     * 日志记录
     */
    void doLog();

    /**
     * @return AI的prompt
     */
    String prompt();
}
