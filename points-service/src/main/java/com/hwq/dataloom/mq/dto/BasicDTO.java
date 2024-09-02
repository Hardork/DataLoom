package com.hwq.dataloom.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HWQ
 * @date 2024/8/26 17:36
 * @description rocketMq 消息基础类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicDTO {

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 主题
     *
     */
    private String topic;

    /**
     * 子主题(比topic低一级，可以用来区分同一topic下的不同业务类型的消息)
     */
    private String tag;

    /**
     * 唯一标识
     */
    private String key;

    /**
     * 超时时间
     */
    private Long timeout;

    /**
     * 延迟消息
     */
    private Long delayTime;

}
