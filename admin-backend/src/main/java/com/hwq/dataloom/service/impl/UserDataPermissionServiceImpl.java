package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.user_data.ShareUserDataRequest;
import com.hwq.dataloom.model.dto.user_data.UserDataSecret;
import com.hwq.dataloom.model.dto.user_data_permission.UserDataPermissionQuery;
import com.hwq.dataloom.model.dto.user_data_permission.UserDataPermissionSave;
import com.hwq.dataloom.model.entity.UserData;
import com.hwq.dataloom.model.entity.UserDataPermission;
import com.hwq.dataloom.model.enums.UserDataPermissionEnum;
import com.hwq.dataloom.model.enums.UserDataPermissionRoleEnum;
import com.hwq.dataloom.service.UserDataPermissionService;
import com.hwq.dataloom.mapper.UserDataPermissionMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author wqh
* @description 针对表【user_data_permission】的数据库操作Service实现
* @createDate 2024-05-01 10:33:28
*/
@Service
public class UserDataPermissionServiceImpl extends ServiceImpl<UserDataPermissionMapper, UserDataPermission>
    implements UserDataPermissionService{


    @Override
    public UserDataPermission queryUserDataPermission(UserDataPermissionQuery userDataPermissionQuery) {
        LambdaQueryWrapper<UserDataPermission> qw = new LambdaQueryWrapper<>();
        qw.eq(UserDataPermission::getDataId, userDataPermissionQuery.getUserId())
                .eq(UserDataPermission::getDataId, userDataPermissionQuery.getUserId());
        return getOne(qw);
    }

    @Override
    public boolean saveUserDataPermission(UserDataPermissionSave userDataPermissionSave) {

        UserDataPermission userDataPermission = UserDataPermission.builder()
                .userId(userDataPermissionSave.getUserId())
                .dataId(userDataPermissionSave.getUserId())
                .permission(userDataPermissionSave.getPermission())
                .role(userDataPermissionSave.getRole())
                .build();


        return save(userDataPermission);
    }

    @Override
    public boolean authorization(Long dataId, Long id, Integer type) {

        // 1.判断当前用户是否在权限表中已经存在
        UserDataPermissionQuery userDataPermissionQuery = UserDataPermissionQuery.builder()
                .userId(id)
                .dataId(dataId)
                .build();
        UserDataPermission permission = this.queryUserDataPermission(userDataPermissionQuery);
        if (permission != null && permission.getPermission() >= type) { // 说明当前用户已经在权限表中，并且申请的权限小于等于当前的权限，直接返回成功
            return true;
        }
        // 2.不存在，将当前用户插入到权限表
        UserDataPermissionSave userDataPermissionSave = UserDataPermissionSave.builder()
                .userId(dataId)
                .dataId(id)
                .permission(type)
                .role(UserDataPermissionRoleEnum.TEAM_WORKER.getValue())
                .build();
        return this.saveUserDataPermission(userDataPermissionSave);
    }
}




