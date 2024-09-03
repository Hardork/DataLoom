package com.hwq.dataloom.mq.event;

import lombok.Builder;
import lombok.Data;

/**
 * @author HWQ
 * @date 2024/9/3 01:37
 * @description 定时优惠券消息
 */
@Data
@Builder
public class ScheduledCouponEvent {
    /**
     * 任务id
     */
    private Long couponTaskId;

    /**
     * 发放类型 0-读取文件发放 1-给所有用户发放(不包括封号用户)
     */
    private Integer distributeType;
}
