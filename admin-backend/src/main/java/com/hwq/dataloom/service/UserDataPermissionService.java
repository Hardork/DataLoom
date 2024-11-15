package com.hwq.dataloom.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.user_data.ShareUserDataRequest;
import com.hwq.dataloom.model.dto.user_data.UserDataSecret;
import com.hwq.dataloom.model.dto.user_data_permission.UserDataPermissionQuery;
import com.hwq.dataloom.model.dto.user_data_permission.UserDataPermissionSave;
import com.hwq.dataloom.model.entity.UserDataPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wqh
* @description 针对表【user_data_permission】的数据库操作Service
* @createDate 2024-05-01 10:33:28
*/
public interface UserDataPermissionService extends IService<UserDataPermission> {

    UserDataPermission queryUserDataPermission(UserDataPermissionQuery userDataPermissionQuery);

    boolean saveUserDataPermission(UserDataPermissionSave userDataPermissionSave);

    boolean authorization(Long dataId, Long id, Integer type);

    List<Long> queryUserIdByDataId(Long dataId);
}
