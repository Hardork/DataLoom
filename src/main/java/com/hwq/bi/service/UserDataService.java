package com.hwq.bi.service;

import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.entity.UserData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author wqh
* @description 针对表【user_data】的数据库操作Service
* @createDate 2024-04-25 20:54:55
*/
public interface UserDataService extends IService<UserData> {

    /**
     * 删除用户数据集
     * @param id
     * @param loginUser
     * @return
     */
    Boolean deleteUserData(Long id, User loginUser);

    /**
     * 创建用户数据集，并设置权限
     */
    Long save(User loginUser, String dataName, String description);
}
