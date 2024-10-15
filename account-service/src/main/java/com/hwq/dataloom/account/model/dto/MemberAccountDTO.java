package com.hwq.dataloom.account.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description:
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAccountDTO {

    private Date vipExpireTime;

    private Long remainDay;

    private Boolean isVIP;

}
