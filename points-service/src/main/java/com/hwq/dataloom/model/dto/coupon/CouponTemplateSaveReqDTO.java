package com.hwq.dataloom.model.dto.coupon;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author HWQ
 * @date 2024/8/27 14:14
 * @description 新增优惠券模版请求类
 */
@Data
@Builder
public class CouponTemplateSaveReqDTO {
    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 有效期开始时间
     */
    @TableField("valid_start_time")
    private Date validStartTime;

    /**
     * 有效期截止时间
     */
    @TableField("valid_end_time")
    private Date validEndTime;

    /**
     * 优惠券发行量
     */
    private Integer stock;

    /**
     * 领取规则
     */
    private Object claimRules;

    /**
     * 使用规则
     */
    private Object usageRules;
}
