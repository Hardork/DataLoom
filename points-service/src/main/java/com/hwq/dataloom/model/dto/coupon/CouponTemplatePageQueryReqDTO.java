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

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 状态 0-正常使用中 1-下线
     */
    private Integer status;
}
