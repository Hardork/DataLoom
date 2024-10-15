package com.hwq.dataloom.framework.service;


import com.hwq.dataloom.framework.model.entity.UserCreditAccount;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description: 积分账户服务
 **/
public interface InnerUserCreditAccountService  {


    UserCreditAccount queryUserCreditAccount(Long userId);
}
