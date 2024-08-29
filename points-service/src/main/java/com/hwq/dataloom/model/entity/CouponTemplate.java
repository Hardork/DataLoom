package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName coupon_template
 */
@TableName(value ="coupon_template")
@Data
public class CouponTemplate implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
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
     * 状态 0-正常使用中 1-下线
     */
    private Integer status;

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
    private String claimRules;

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

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}