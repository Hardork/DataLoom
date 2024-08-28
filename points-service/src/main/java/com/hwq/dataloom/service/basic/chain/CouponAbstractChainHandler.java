package com.hwq.dataloom.service.basic.chain;

import org.springframework.core.Ordered;

/**
 * @author HWQ
 * @date 2024/8/27 01:01
 * @description 责任链处理器接口
 */
public interface CouponAbstractChainHandler<T> extends Ordered {

    /**
     * 处理器唯一标识
     * @return
     */
    String mark();

    /**
     * 执行责任链逻辑
     * @param request 处理参数
     */
    void handle(T request);
}
