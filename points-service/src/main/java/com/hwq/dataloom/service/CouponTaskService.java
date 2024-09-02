package com.hwq.dataloom.service;

import com.hwq.dataloom.model.dto.coupon_task.CouponTaskCreateReqDTO;
import com.hwq.dataloom.model.entity.CouponTask;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author wqh
* @description 针对表【coupon_task(优惠券模板发送任务表)】的数据库操作Service
* @createDate 2024-08-30 17:07:36
*/
public interface CouponTaskService extends IService<CouponTask> {


    /**
     * 手动进行优惠券发放
     * @param couponTaskCreateReqDTO 手动优惠券发放请求类
     */
    void manuallySendCouponTask(CouponTaskCreateReqDTO couponTaskCreateReqDTO);
}
