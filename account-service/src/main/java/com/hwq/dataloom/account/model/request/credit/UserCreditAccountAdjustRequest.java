package com.hwq.dataloom.account.model.request.credit;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description:
 **/
@Data
public class UserCreditAccountAdjustRequest {

    private Long userId;

    private BigDecimal amount;
}
