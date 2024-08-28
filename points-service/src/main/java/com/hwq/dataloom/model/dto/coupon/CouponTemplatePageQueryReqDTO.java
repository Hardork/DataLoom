package com.hwq.dataloom.model.dto.coupon;

import com.hwq.dataloom.framework.request.PageRequest;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author HWQ
 * @date 2024/8/27 14:21
 * @description 分页查询请求类
 */
@Data
@Builder
public class CouponTemplatePageQueryReqDTO extends PageRequest {
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
