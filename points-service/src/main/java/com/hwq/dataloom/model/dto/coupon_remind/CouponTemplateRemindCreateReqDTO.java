package com.hwq.dataloom.model.dto.coupon_remind;

import lombok.Data;

/**
 * 抢券提醒请求类
 */
@Data
public class CouponTemplateRemindCreateReqDTO {

    /**
     * 优惠券模板id
     */
    private Long couponTemplateId;

    /**
     * 用户联系方式，可能是邮箱、手机号、等等
     */
    private String contact;

    /**
     * 提醒方式
     */
    private Integer type;

    /**
     * 提醒时间，比如五分钟，十分钟，十五分钟
     */
    private Integer remindTime;


}