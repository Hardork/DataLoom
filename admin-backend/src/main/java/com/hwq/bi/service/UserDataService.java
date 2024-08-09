package com.hwq.bi.service;

import com.hwq.bi.model.dto.datasource.TableFieldInfo;
import com.hwq.bi.model.dto.user_data.ShareUserDataRequest;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.entity.UserData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.bi.model.vo.DataCollaboratorsVO;
import org.springframework.web.multipart.MultipartFile;

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
    Long save(User loginUser, String dataName, String description, MultipartFile multipartFile);

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
     * 获取数据集的所有数据协作者
     * @param dataId
     * @param loginUser
     * @return
     */
    List<DataCollaboratorsVO> getDataCollaborators(Long dataId, User loginUser);

    /**
     * 将数据集存储到MySQL中
     * @param loginUser
     * @param dataName
     * @param description
     * @param multipartFile
     * @return
     */
    Long saveToMySQL(User loginUser, String dataName, String description, List<TableFieldInfo> tableFieldInfos, MultipartFile multipartFile);

    /**
     * 显示MySQL数据集
     * @param loginUser
     * @return
     */
    List<UserData> listMySQLByPermission(User loginUser);
}
