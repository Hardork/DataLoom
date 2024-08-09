package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.constant.RewardRecordConstant;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.entity.RewardRecord;
import com.hwq.dataloom.model.entity.User;
import com.hwq.dataloom.service.RewardRecordService;
import com.hwq.dataloom.mapper.RewardRecordMapper;
import com.hwq.dataloom.service.UserService;
import com.hwq.dataloom.service.impl.role_info.RoleService;
import com.hwq.dataloom.service.impl.role_info.RoleStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    @Resource
    private RoleStrategyFactory roleStrategyFactory;

    @Override
    @Transactional
    public boolean addReward(User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        synchronized (loginUser.getUserAccount().intern()) {
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
            // 根据用户的身份信息修改用户的积分
            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
            // 使用策略模式，根据不同的角色进行不同的策略
            RoleService roleService = roleStrategyFactory.getRoleStrategy(loginUser.getUserRole());
            ThrowUtils.throwIf(roleService == null, ErrorCode.PARAMS_ERROR, "没有对应角色");
            userUpdateWrapper.eq("id", loginUser.getId()).setSql("totalRewardPoints = totalRewardPoints + " + roleService.getDayReward());
            boolean update = userService.update(userUpdateWrapper);
            ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        }
        return true;
    }
}




