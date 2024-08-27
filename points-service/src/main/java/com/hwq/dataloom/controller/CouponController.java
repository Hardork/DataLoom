package com.hwq.dataloom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwq.dataloom.annotation.NoRepeatSubmit;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.coupon.CouponTemplateNumberReqDTO;
import com.hwq.dataloom.model.dto.coupon.CouponTemplatePageQueryReqDTO;
import com.hwq.dataloom.model.dto.coupon.CouponTemplateSaveReqDTO;
import com.hwq.dataloom.model.vo.coupon.CouponTemplatePageQueryVO;
import com.hwq.dataloom.model.vo.coupon.CouponTemplateQueryVO;
import com.hwq.dataloom.service.CouponTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/8/27 12:10
 * @description 优惠券增删改查
 */
@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Resource
    private CouponTemplateService couponTemplateService;

    @Operation(summary = "创建优惠券模板")
    @NoRepeatSubmit(message = "请勿短时间内重复提交优惠券模板")
    @PostMapping("create")
    public BaseResponse<Boolean> createCouponTemplate(@RequestBody CouponTemplateSaveReqDTO requestParam) {
        couponTemplateService.createCouponTemplate(requestParam);
        return ResultUtils.success(Boolean.TRUE);
    }

    @Operation(summary = "分页查询优惠券模板")
    @GetMapping("/page")
    public BaseResponse<Page<CouponTemplatePageQueryVO>> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam) {
        return ResultUtils.success(couponTemplateService.pageQueryCouponTemplate(requestParam));
    }

    @Operation(summary = "查询优惠券模板详情")
    @GetMapping("/getById")
    public BaseResponse<CouponTemplateQueryVO> findCouponTemplate(Long couponTemplateId) {
        return ResultUtils.success(couponTemplateService.findCouponTemplateById(couponTemplateId));
    }

    @Operation(summary = "增加优惠券模板发行量")
    @NoRepeatSubmit(message = "请勿短时间内重复增加优惠券发行量")
    @PostMapping("/increase-number")
    public BaseResponse<Void> increaseNumberCouponTemplate(@RequestBody CouponTemplateNumberReqDTO requestParam) {
        couponTemplateService.increaseNumberCouponTemplate(requestParam);
        return ResultUtils.success();
    }

    @Operation(summary = "结束优惠券模板")
    @PostMapping("/terminate")
    public BaseResponse<Void> terminateCouponTemplate(Long couponTemplateId) {
        couponTemplateService.terminateCouponTemplate(couponTemplateId);
        return ResultUtils.success();
    }
}
