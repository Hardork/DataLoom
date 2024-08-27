package com.hwq.dataloom.model.vo.coupon;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author HWQ
 * @date 2024/8/27 14:19
 * @description 分页查询优惠券返回类
 */
@Data
@Builder
public class CouponTemplatePageQueryVO {
    private Long id;

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
    private Date validStartTime;

    /**
     * 有效期截止时间
     */
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
