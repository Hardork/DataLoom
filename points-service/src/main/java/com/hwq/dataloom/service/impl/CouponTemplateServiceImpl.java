package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.model.dto.coupon.CouponTemplateNumberReqDTO;
import com.hwq.dataloom.model.dto.coupon.CouponTemplatePageQueryReqDTO;
import com.hwq.dataloom.model.dto.coupon.CouponTemplateSaveReqDTO;
import com.hwq.dataloom.model.entity.CouponTemplate;
import com.hwq.dataloom.model.vo.coupon.CouponTemplatePageQueryVO;
import com.hwq.dataloom.model.vo.coupon.CouponTemplateQueryVO;
import com.hwq.dataloom.service.CouponTemplateService;
import com.hwq.dataloom.mapper.CouponTemplateMapper;
import com.hwq.dataloom.service.basic.chain.CouponAbstractChainHandler;
import com.hwq.dataloom.service.basic.chain.CouponChainContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.HashMap;

import static com.hwq.dataloom.constants.CouponConstant.CREATE_COUPON_TEMPLATE_MASK;

/**
* @author wqh
* @description 针对表【coupon_template】的数据库操作Service实现
* @createDate 2024-08-27 13:03:15
*/
@Service
public class CouponTemplateServiceImpl extends ServiceImpl<CouponTemplateMapper, CouponTemplate>
    implements CouponTemplateService{

    @Resource
    private CouponChainContext couponChainContext;

    @Override
    public void createCouponTemplate(CouponTemplateSaveReqDTO requestParam) {
        // TODO：使用责任链串联校验
        couponChainContext.handle(CREATE_COUPON_TEMPLATE_MASK, requestParam);
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
    }

    @Override
    public Page<CouponTemplatePageQueryVO> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam) {
        return null;
    }

    @Override
    public CouponTemplateQueryVO findCouponTemplateById(Long couponTemplateId) {
        return null;
    }

    @Override
    public void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam) {

    }

    @Override
    public void terminateCouponTemplate(Long couponTemplateId) {

    }
}




