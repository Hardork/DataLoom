package com.hwq.bi.service.impl;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.dto.user_data.ShareUserDataRequest;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.entity.UserData;
import com.hwq.bi.model.entity.UserDataPermission;
import com.hwq.bi.model.enums.UserDataPermissionEnum;
import com.hwq.bi.model.enums.UserDataTypeEnum;
import com.hwq.bi.service.UserDataService;
import com.hwq.bi.mapper.UserDataMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import static com.hwq.bi.constant.UserDataConstant.SECRET_SALT;

/**
* @author wqh
* @description 针对表【user_data】的数据库操作Service实现
* @createDate 2024-04-25 20:54:55
*/
@EqualsAndHashCode(callSuper = true)
@Service
@ConfigurationProperties(prefix = "backend")
@Data
public class UserDataServiceImpl extends ServiceImpl<UserDataMapper, UserData>
    implements UserDataService{


    private String protocol;

    private String ip;

    private String port;

    private String prefix;

    @Override
    public Boolean deleteUserData(Long id, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 删除userData中的数据
        UserData userData = getById(id);
        ThrowUtils.throwIf(userData == null, ErrorCode.PARAMS_ERROR, "删除数据集不存在");
        ThrowUtils.throwIf(!userData.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        boolean delete = removeById(id);
        ThrowUtils.throwIf(!delete, ErrorCode.SYSTEM_ERROR);
        // 删除mongoDB中的数据
        return true;
    }


    @Override
    public Long save(User loginUser, String dataName, String description) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(dataName), ErrorCode.NOT_LOGIN_ERROR);
        UserData userData = new UserData();
        userData.setUserId(loginUser.getId());
        userData.setDataName(dataName);
        userData.setDescription(description);
        userData.setUploadType(UserDataTypeEnum.EXCEL.getValue());
        // 生成对应读写密钥
        String readSecretKey = DigestUtil.md5Hex(SECRET_SALT + loginUser.getUserAccount() + RandomUtil.randomNumbers(5));
        String writeSecretKey = DigestUtil.md5Hex(SECRET_SALT + loginUser.getUserAccount() + RandomUtil.randomNumbers(8));
        userData.setReadSecretKey(readSecretKey);
        userData.setWriteSecretKey(writeSecretKey);
        // 生成数据集元数据表
        boolean save = this.save(userData);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);

        // 生成数据集权限表
        UserDataPermission userDataPermission = new UserDataPermission();
        userDataPermission.setDataId(userData.getId());
        userDataPermission.setUserId(loginUser.getId());
        userDataPermission.setPermission(UserDataPermissionEnum.WRITE.getValue());

        return userData.getId();
    }

    @Override
    public String genLink(ShareUserDataRequest shareUserDataRequest, User loginUser) {
        Long id = shareUserDataRequest.getId();
        Integer permission = shareUserDataRequest.getPermission();
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(permission == null, ErrorCode.PARAMS_ERROR);
        // 鉴权
        UserData userData = this.getById(id);
        ThrowUtils.throwIf(!userData.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        // 生成链接
        StringBuilder link = new StringBuilder();
        link.append(protocol).append("://").append(ip).append(":").append(port).append("/").append(prefix).append("/").append(id).append("/");
        UserDataPermissionEnum targetPermission = UserDataPermissionEnum.getEnumByValue(permission);
        ThrowUtils.throwIf(targetPermission == null, ErrorCode.PARAMS_ERROR);
        if (targetPermission.equals(UserDataPermissionEnum.READ)) { // 读请求
            link.append(UserDataPermissionEnum.READ.getValue());
            link.append("/");
            link.append(userData.getReadSecretKey());
        } else {
            link.append(UserDataPermissionEnum.WRITE.getValue());
            link.append("/");
            link.append(userData.getWriteSecretKey());
        }
        return link.toString();
    }
}




