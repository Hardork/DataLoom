package com.hwq.dataloom.service;

import com.hwq.dataloom.model.dto.coupon_remind.CouponTemplateRemindCreateReqDTO;
import com.hwq.dataloom.model.entity.CouponTemplateRemind;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author wqh
* @description 针对表【coupon_template_remind(用户预约提醒信息存储表)】的数据库操作Service
* @createDate 2024-09-04 02:39:34
*/
public interface CouponTemplateRemindService extends IService<CouponTemplateRemind> {

    /**
     * 创建优惠券抢券提醒
     * @param requestParam 请求类
     * @return 是否预约成功
     */
    Boolean createCouponRemind(CouponTemplateRemindCreateReqDTO requestParam);
}
