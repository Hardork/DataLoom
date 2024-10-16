package com.hwq.dataloom.account.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hwq.dataloom.account.service.UserCreditAccountService;
import com.hwq.dataloom.framework.model.entity.UserCreditAccount;
import com.hwq.dataloom.framework.service.InnerUserCreditAccountService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description:
 **/
@DubboService
public class InnerUserCreditAccountServiceImpl implements InnerUserCreditAccountService {

    @Resource
    private UserCreditAccountService userCreditAccountService;
    @Override
    public UserCreditAccount queryUserCreditAccount(Long userId) {
        UserCreditAccount userCreditAccount = userCreditAccountService.getOne(new LambdaUpdateWrapper<UserCreditAccount>().eq(UserCreditAccount::getUserId, userId));
        return userCreditAccount;
    }
}
