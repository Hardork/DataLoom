package com.hwq.dataloom.service.basic.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.model.dto.coupon.CouponTemplateSaveReqDTO;
import com.hwq.dataloom.service.basic.chain.CouponAbstractChainHandler;
import org.springframework.stereotype.Component;

import static com.hwq.dataloom.constants.CouponConstant.CREATE_COUPON_TEMPLATE_MASK;

/**
 * 创建优惠券模版 - 非空校验处理器
 * order: 0
 */
@Component
public class CheckCouponTemplateCreateNotNullChainHandler implements CouponAbstractChainHandler<CouponTemplateSaveReqDTO> {

    @Override
    public void handle(CouponTemplateSaveReqDTO requestParam) {
        if (StrUtil.isEmpty(requestParam.getName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券名称不能为空");
        }
        if (ObjectUtil.isEmpty(requestParam.getType())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠类型不能为空");
        }
        if (ObjectUtil.isEmpty(requestParam.getValidStartTime())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "有效期开始时间不能为空");
        }
        if (ObjectUtil.isEmpty(requestParam.getValidEndTime())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "有效期结束时间不能为空");
        }
        if (ObjectUtil.isEmpty(requestParam.getStock())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存不能为空");
        }
        if (StrUtil.isEmpty(requestParam.getClaimRules())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "领取规则不能为空");
        }
        if (StrUtil.isEmpty(requestParam.getUsageRules())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消耗规则不能为空");
        }
    }

    @Override
    public String mark() {
        return CREATE_COUPON_TEMPLATE_MASK;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
