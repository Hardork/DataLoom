package com.hwq.dataloom.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.account.mapper.MemberAccountMapper;
import com.hwq.dataloom.account.model.dto.MemberAccountDTO;
import com.hwq.dataloom.account.model.entity.MemberAccount;
import com.hwq.dataloom.account.service.MemberAccountService;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
public class MemberAccountServiceImpl extends ServiceImpl<MemberAccountMapper, MemberAccount>
        implements MemberAccountService {


    @Override
    public Boolean isMemberAccount(Long userId) {
        MemberAccount memberAccount = getById(userId);
        Date vipExpireTime = memberAccount.getVipExpireTime();
        return vipExpireTime!=null && vipExpireTime.after(new Date());
    }

    @Override
    public Boolean createMemberAccount(Long userId) {
        MemberAccount memberAccount = new MemberAccount();
        memberAccount.setUserId(userId);
        return save(memberAccount);
    }

    @Override
    public MemberAccountDTO queryMemberAccount(Long userId) {
        MemberAccount memberAccount = getOne(new LambdaQueryWrapper<MemberAccount>()
                .eq(MemberAccount::getUserId, userId));
        Date vipExpireTime = memberAccount.getVipExpireTime();
        Date now = new Date();
        MemberAccountDTO memberAccountDTO = MemberAccountDTO.builder()
                .isVIP(vipExpireTime!=null && vipExpireTime.after(now))
                .remainDay(vipExpireTime!=null ? getDaysBetween(vipExpireTime,now) : 0)
                .build();
        return memberAccountDTO;
    }

    // 计算两个 Date 对象的天数差
    public static long getDaysBetween(Date startDate, Date endDate) {
        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());  // 获取时间差的毫秒数
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);      // 将毫秒转换为天数
    }


}
