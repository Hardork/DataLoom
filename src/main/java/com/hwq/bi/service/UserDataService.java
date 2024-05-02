package com.hwq.bi.service;

import com.hwq.bi.model.dto.user_data.ShareUserDataRequest;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.entity.UserData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    /**
     * 生成数据集分享链接
     * @param shareUserDataRequest
     * @param loginUser
     * @return
     */
    String genLink(ShareUserDataRequest shareUserDataRequest, User loginUser);

    /**
     * 校验链接并授权
     * @param dataId
     * @param type
     * @param secret
     * @param loginUser
     * @return
     */
    Boolean checkLinkAndAuthorization(Long dataId, Integer type, String secret, User loginUser);

    List<UserData> listByPermission(User loginUser);

    /**
     * 获取数据协作与权限
     * @param dataId
     * @param loginUser
     * @return
     */
    List<User> getDataCollaborators(Long dataId, User loginUser);
}
