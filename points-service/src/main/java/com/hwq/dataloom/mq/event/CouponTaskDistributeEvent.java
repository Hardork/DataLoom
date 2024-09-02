package com.hwq.dataloom.mq.event;

import lombok.Builder;
import lombok.Data;

/**
 * @author HWQ
 * @date 2024/9/1 21:25
 * @description 优惠券发放事件
 */
@Data
@Builder
public class CouponTaskDistributeEvent {
    /**
     * 优惠券分发任务id
     */
    private Long couponTaskId;

    /**
     * 优惠券分发任务批量id
     */
    private Long couponTaskBatchId;

    /**
     * 通知方式，可组合使用 0：站内信 1：弹框推送 2：邮箱 3：短信
     */
    private String notifyType;


    /**
     * 优惠券模板id
     */
    private Long couponTemplateId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 使用规则
     */
    private String usageRules;


    /**
     * 批量保存用户优惠券 Set 长度，默认满 5000 才会批量保存数据库
     */
    private Integer batchUserSetSize;

    /**
     * 分发结束标识
     */
    private Boolean distributionEndFlag;
}
