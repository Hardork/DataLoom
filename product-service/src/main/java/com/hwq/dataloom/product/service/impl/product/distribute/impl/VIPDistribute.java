package com.hwq.dataloom.product.service.impl.product.distribute.impl;

import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.model.request.MemberAccountOvertimeRequest;
import com.hwq.dataloom.framework.service.InnerMemberAccountService;
import com.hwq.dataloom.product.model.entity.Product;
import com.hwq.dataloom.product.mq.event.ProductDistributeEvent;
import com.hwq.dataloom.product.service.impl.product.distribute.DistributeProduct;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/16
 * @Description:
 **/
@Component("VIP")
public class VIPDistribute implements DistributeProduct {
    @DubboReference
    public InnerMemberAccountService innerMemberAccountService;
    @Override
    public void giveOutProduct(ProductDistributeEvent productDistributeEvent) {
        Product product = productDistributeEvent.getProduct();
        String productConfig = product.getProductConfig();
        String[] split = productConfig.split(":");
        if(split.length != 2){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MemberAccountOvertimeRequest memberAccountOvertimeRequest = new MemberAccountOvertimeRequest();
        memberAccountOvertimeRequest.setTime(Integer.parseInt(split[0]));
        memberAccountOvertimeRequest.setOverTimeUnit(split[1]);
        memberAccountOvertimeRequest.setUserId(productDistributeEvent.getUserId());
        innerMemberAccountService.overtime(memberAccountOvertimeRequest);
    }
}
