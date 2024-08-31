package com.hwq.dataloom.model.dto.coupon_task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HWQ
 * @date 2024/8/30 15:42
 * @description 定期发送优惠券请求类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledSendCouponTaskDTO {
    /**
     * 优惠券模版id
     */
    private Long couponTemplateId;

    /**
     * 覆盖的范围
     * all - 全部用户
     * vip - vip用户
     * sVip - sVip用户
     */
    private String scope;


}
