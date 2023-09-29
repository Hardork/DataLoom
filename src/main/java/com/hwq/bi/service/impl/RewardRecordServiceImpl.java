package com.hwq.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.constant.RewardRecordConstant;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.entity.RewardRecord;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.service.RewardRecordService;
import com.hwq.bi.mapper.RewardRecordMapper;
import com.hwq.bi.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
* @author HWQ
* @description 针对表【reward_record(奖励积分领取表)】的数据库操作Service实现
* @createDate 2023-09-22 21:40:59
*/
@Service
public class RewardRecordServiceImpl extends ServiceImpl<RewardRecordMapper, RewardRecord>
    implements RewardRecordService{

    @Resource
    private UserService userService;
    @Resource
    private RewardRecordMapper rewardRecordMapper;

    @Override
    @Transactional
    public boolean addReward(User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 查询当前用户今日是否已经获取
        QueryWrapper<RewardRecord> qw = new QueryWrapper<>();
        Long userId = loginUser.getId();
        LocalDateTime now = LocalDateTime.now();
        List<RewardRecord> rewardRecords = rewardRecordMapper.judgeTodayHasAdd(userId, now);
        if (!rewardRecords.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "今日已领取");
        }
        // 获取
        RewardRecord rewardRecord = new RewardRecord();
        rewardRecord.setRewardPoints(RewardRecordConstant.DAY_FREE_NUM);
        rewardRecord.setUserId(loginUser.getId());
        boolean save = this.save(rewardRecord);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        // 修改用户的积分
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", loginUser.getId()).setSql("totalRewardPoints = totalRewardPoints + " + RewardRecordConstant.DAY_FREE_NUM);
        boolean update = userService.update(userUpdateWrapper);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        return true;
    }
}




