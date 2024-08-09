package com.hwq.bi.service;

import com.hwq.bi.model.entity.RewardRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.bi.model.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author HWQ
* @description 针对表【reward_record(奖励积分领取表)】的数据库操作Service
* @createDate 2023-09-22 21:41:00
*/
public interface RewardRecordService extends IService<RewardRecord> {

    /**
     * 用户每日获取免费分析次数(10次)
     * @param loginUser
     * @return
     */
    boolean addReward(User loginUser);
}
