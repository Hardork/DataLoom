package com.hwq.bi.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.entity.UserData;
import com.hwq.bi.model.enums.UserDataTypeEnum;
import com.hwq.bi.service.MongoService;
import com.hwq.bi.service.UserDataService;
import com.hwq.bi.mapper.UserDataMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.hwq.bi.constant.UserDataConstant.SECRET_SALT;

/**
* @author wqh
* @description 针对表【user_data】的数据库操作Service实现
* @createDate 2024-04-25 20:54:55
*/
@Service
public class UserDataServiceImpl extends ServiceImpl<UserDataMapper, UserData>
    implements UserDataService{


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
        // 更新
        // 生成对应读写密钥
        String readSecretKey = DigestUtil.md5Hex(SECRET_SALT + loginUser.getUserAccount() + RandomUtil.randomNumbers(5));
        String writeSecretKey = DigestUtil.md5Hex(SECRET_SALT + loginUser.getUserAccount() + RandomUtil.randomNumbers(8));
        userData.setReadSecretKey(readSecretKey);
        userData.setWriteSecretKey(writeSecretKey);
        boolean save = this.save(userData);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        return userData.getId();
    }
}




