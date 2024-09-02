package com.hwq.dataloom.mapper;

import com.hwq.dataloom.model.dto.coupon.CouponTemplateNumberReqDTO;
import com.hwq.dataloom.model.entity.CouponTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwq.dataloom.mq.event.CouponTaskDistributeEvent;
import org.apache.ibatis.annotations.Param;

/**
* @author wqh
* @description 针对表【coupon_template】的数据库操作Mapper
* @createDate 2024-08-27 13:03:15
* @Entity com.hwq.dataloom.model.entity.CouponTemplate
*/
public interface CouponTemplateMapper extends BaseMapper<CouponTemplate> {

    int increaseNumberCouponTemplate(@Param("requestParam") CouponTemplateNumberReqDTO requestParam);

    int decrementCouponTemplateStock(@Param("couponTemplateId") Long couponTemplateId, @Param("batchUserSetSize")Integer batchUserSetSize);

}




