package com.hwq.dataloom.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.account.model.dto.MemberAccountDTO;
import com.hwq.dataloom.account.model.entity.MemberAccount;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
public interface MemberAccountService extends IService<MemberAccount> {
    Boolean isMemberAccount(Long userId);

    Boolean createMemberAccount(Long userId);

    MemberAccountDTO queryMemberAccount(Long userId);

}
