package com.hwq.dataloom.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.account.mapper.UserCreditAccountMapper;
import com.hwq.dataloom.account.model.request.credit.UserCreditAccountAdjustRequest;
import com.hwq.dataloom.account.model.entity.UserCreditAccount;
import com.hwq.dataloom.account.service.UserCreditAccountService;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
public class UserCreditAccountServiceImpl extends ServiceImpl<UserCreditAccountMapper, UserCreditAccount>
        implements UserCreditAccountService {
    @Override
    public Boolean adjustCredit(UserCreditAccountAdjustRequest userCreditAccountAdjustRequest) {

        return update(new LambdaUpdateWrapper<UserCreditAccount>()
                .setSql("availableAmount = availableAmount +" + userCreditAccountAdjustRequest.getAmount())
                .eq(UserCreditAccount::getUserId, userCreditAccountAdjustRequest.getUserId()));
    }

    @Override
    public Boolean createUserCreditAccount(Long userId) {
        UserCreditAccount userCreditAccount = new UserCreditAccount();
        userCreditAccount.setUserId(userId);
        return save(userCreditAccount);
    }
}
