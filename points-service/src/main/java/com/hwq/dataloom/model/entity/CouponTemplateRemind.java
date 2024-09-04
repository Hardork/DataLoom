package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 用户预约提醒信息存储表
 * @TableName coupon_template_remind
 */
@TableName(value ="coupon_template_remind")
@Data
@Builder
public class CouponTemplateRemind implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 券ID
     */
    private Long couponTemplateId;

    /**
     * 存储信息
     */
    private Long information;

    /**
     * 优惠券开抢时间
     */
    private Date startTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}