package com.hwq.dataloom.service.basic.handler;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.model.dto.coupon.CouponTemplateSaveReqDTO;
import com.hwq.dataloom.model.enums.CouponTypeEnum;
import com.hwq.dataloom.service.basic.chain.CouponAbstractChainHandler;
import org.springframework.stereotype.Component;

import static com.hwq.dataloom.constants.CouponConstant.CREATE_COUPON_TEMPLATE_MASK;

/**
 * 创建优惠券模版 - 逻辑校验处理器
 * order: 1
 */
@Component
public class CheckCouponTemplateCreateValidChainHandler implements CouponAbstractChainHandler<CouponTemplateSaveReqDTO> {

    @Override
    public void handle(CouponTemplateSaveReqDTO requestParam) {
        Integer type = requestParam.getType();
        Date validEndTime = requestParam.getValidEndTime();
        Integer stock = requestParam.getStock();
        String claimRules = requestParam.getClaimRules();
        String usageRules = requestParam.getUsageRules();

        if (validEndTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券结束日期不得创建日期");
        }
        if (CouponTypeEnum.findValueByType(type) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券类型不存在");
        }
        if (stock < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券发行量异常");
        }
        if (!JSON.isValid(claimRules)) {
            // 此处已经基本能判断数据请求属于恶意攻击，可以上报风控中心进行封禁账号
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券领取规则异常");
        }
        if (!JSON.isValid(usageRules)) {
            // 此处已经基本能判断数据请求属于恶意攻击，可以上报风控中心进行封禁账号
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券使用规则异常");
        }
    }

    @Override
    public String mark() {
        return CREATE_COUPON_TEMPLATE_MASK;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
