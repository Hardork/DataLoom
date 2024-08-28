package com.hwq.dataloom.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwq.dataloom.model.dto.coupon.CouponTemplateNumberReqDTO;
import com.hwq.dataloom.model.dto.coupon.CouponTemplatePageQueryReqDTO;
import com.hwq.dataloom.model.dto.coupon.CouponTemplateSaveReqDTO;
import com.hwq.dataloom.model.entity.CouponTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.model.vo.coupon.CouponTemplatePageQueryVO;
import com.hwq.dataloom.model.vo.coupon.CouponTemplateQueryVO;

/**
* @author wqh
* @description 针对表【coupon_template】的数据库操作Service
* @createDate 2024-08-27 13:03:15
*/
public interface CouponTemplateService extends IService<CouponTemplate> {

    /**
     * 创建优惠券模版
     * @param requestParam 请求类
     */
    void createCouponTemplate(CouponTemplateSaveReqDTO requestParam);

    /**
     * 分页查询优惠券模版
     * @param requestParam 请求类
     * @return
     */
    Page<CouponTemplatePageQueryVO> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam);

    /**
     * 根据 id 查询单条优惠券模版
     * @param couponTemplateId 优惠券id
     * @return
     */
    CouponTemplateQueryVO findCouponTemplateById(Long couponTemplateId);

    /**
     * 根据 id 增加单条优惠券模版的发行量
     * @param requestParam  请求类
     */
    void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam);

    /**
     * 根据 id 下线单条优惠券模版
     * @param couponTemplateId 优惠券id
     */
    void terminateCouponTemplate(Long couponTemplateId);
}
