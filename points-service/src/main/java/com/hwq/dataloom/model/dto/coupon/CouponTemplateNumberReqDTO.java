package com.hwq.dataloom.model.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "优惠券模板增加发行量请求参数")
public class CouponTemplateNumberReqDTO {

    /**
     * 优惠券模板id
     */
    @Schema(description = "优惠券模板id",
            example = "1",
            required = true)
    private Long couponTemplateId;

    /**
     * 增加发行数量
     */
    @Schema(description = "增加发行数量",
            example = "100",
            required = true)
    private Integer number;
}