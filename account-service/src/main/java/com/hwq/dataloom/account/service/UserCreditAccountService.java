package com.hwq.dataloom.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.account.model.request.credit.UserCreditAccountAdjustRequest;
import com.hwq.dataloom.account.model.entity.UserCreditAccount;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description: 积分账户服务
 **/
public interface UserCreditAccountService extends IService<UserCreditAccount> {

    Boolean adjustCredit(UserCreditAccountAdjustRequest userCreditAccountAdjustRequest);

    Boolean createUserCreditAccount(Long userId);
}
