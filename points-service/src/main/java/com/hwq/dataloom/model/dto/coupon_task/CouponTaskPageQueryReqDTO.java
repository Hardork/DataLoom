package com.hwq.dataloom.model.dto.coupon_task;

import com.hwq.dataloom.framework.request.PageRequest;
import lombok.Data;

@Data
public class CouponTaskPageQueryReqDTO extends PageRequest {

    /**
     * 批次id
     */
    private String batchId;

    /**
     * 优惠券批次任务名称
     */
    private String taskName;

    /**
     * 优惠券模板id
     */
    private Long couponTemplateId;

    /**
     * 状态 0：待执行 1：执行中 2：执行失败 3：执行成功 4：取消
     */
    private Integer status;
}
