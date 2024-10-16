package com.hwq.dataloom.framework.service;

import com.hwq.dataloom.framework.model.request.MemberAccountOvertimeRequest;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description:
 **/
public interface InnerMemberAccountService {

    Boolean isMemberAccount(Long userId);

    Boolean overtime(MemberAccountOvertimeRequest memberAccountOvertimeRequest);
}
