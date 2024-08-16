package com.hwq.dataloom.service.impl;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.dto.datasource.TableFieldInfo;
import com.hwq.dataloom.model.dto.user_data.ShareUserDataRequest;
import com.hwq.dataloom.model.entity.DatasourceMetaInfo;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.entity.UserData;
import com.hwq.dataloom.model.entity.UserDataPermission;
import com.hwq.dataloom.model.enums.UserDataPermissionEnum;
import com.hwq.dataloom.model.enums.UserDataTypeEnum;
import com.hwq.dataloom.model.vo.DataCollaboratorsVO;
import com.hwq.dataloom.model.vo.UserVO;
import com.hwq.dataloom.service.DatasourceMetaInfoService;
import com.hwq.dataloom.service.UserDataPermissionService;
import com.hwq.dataloom.service.UserDataService;
import com.hwq.dataloom.mapper.UserDataMapper;
import com.hwq.dataloom.service.UserService;
import com.hwq.dataloom.utils.datasource.ExcelUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hwq.dataloom.constant.UserDataConstant.SECRET_SALT;

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

    @Resource
    private UserDataPermissionService userDataPermissionService;

    @Resource
    private UserService userService;

    @Resource
    private DatasourceMetaInfoService datasourceMetaInfoService;

    @Resource
    private ExcelUtils excelUtils;

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
    @Transactional(rollbackFor = Exception.class)
    public Long save(User loginUser, String dataName, String description, MultipartFile multipartFile) {
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
        boolean savePermission = userDataPermissionService.save(userDataPermission);
        // 将数据存储到MySQL中
        excelUtils.saveDataToMongo(multipartFile, userData.getId());
        ThrowUtils.throwIf(!savePermission, ErrorCode.SYSTEM_ERROR);
        // 将数据存储到MongoDB中
        List<TableFieldInfo> tableFieldInfos = excelUtils.saveDataToMongo(multipartFile, userData.getId());
        // 更新元数据的fieldType
        String tableFieldInfosString = JSON.toJSONString(tableFieldInfos);
        userData.setFieldTypeInfo(tableFieldInfosString);
        boolean update = this.updateById(userData);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        return userData.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
        boolean savePermission = userDataPermissionService.save(userDataPermission);
        ThrowUtils.throwIf(!savePermission, ErrorCode.SYSTEM_ERROR);
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

    @Override
    public Boolean checkLinkAndAuthorization(Long dataId, Integer type, String secret, User loginUser) {
        // 校验参数
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(dataId == null || dataId < 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR, "请求类型为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(secret), ErrorCode.PARAMS_ERROR);
        // 获取对应数据集元数据
        UserData userData = this.getById(dataId);
        ThrowUtils.throwIf(userData == null, ErrorCode.PARAMS_ERROR, "对应数据集不存在");
        // 判断权限类型
        UserDataPermissionEnum userDataPermissionEnum = UserDataPermissionEnum.getEnumByValue(type);
        ThrowUtils.throwIf(userDataPermissionEnum == null, ErrorCode.PARAMS_ERROR);
        // 校验权限
        if (userDataPermissionEnum == UserDataPermissionEnum.READ) { // 读权限
            // 判断密钥是否相符
            ThrowUtils.throwIf(!secret.equals(userData.getReadSecretKey()), ErrorCode.NO_AUTH_ERROR);
        } else { // 写权限
            ThrowUtils.throwIf(!secret.equals(userData.getWriteSecretKey()), ErrorCode.NO_AUTH_ERROR);
        }
        // 判断是否需要审批
        if (userData.getApprovalConfirm()) { // 需要审批
            // todo: 将请求加入审批表，由创建者审批后才能添加到权限表中
            return true;
        }
        // 将当前用户加入到权限表中
        // 1.判断当前用户是否在权限表中已经存在
        QueryWrapper<UserDataPermission> qw = new QueryWrapper<>();
        qw.eq("dataId", dataId);
        qw.eq("userId", loginUser.getId());
        UserDataPermission permission = userDataPermissionService.getOne(qw);
        if (permission != null && permission.getPermission() >= type) { // 说明当前用户已经在权限表中，并且申请的权限小于等于当前的权限，直接返回成功
            return true;
        }
        // 2.不存在，将当前用户插入到权限表
        UserDataPermission userDataPermission = new UserDataPermission();
        userDataPermission.setDataId(dataId);
        userDataPermission.setUserId(loginUser.getId());
        userDataPermission.setPermission(type);
        boolean save = userDataPermissionService.save(userDataPermission);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        return true;
    }

    @Override
    public List<UserData> listByPermission(User loginUser) { // 根据数据集权限表访问
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 获取当前用户具有权限的数据集ID
        QueryWrapper<UserDataPermission> qw = new QueryWrapper<>();
        qw.eq("userId", loginUser.getId());
        qw.select("dataId");
        // 查询对应的excel存储的地方
        List<Long> dataIds = userDataPermissionService.list(qw)
                .stream()
                .map(UserDataPermission::getDataId)
                .collect(Collectors.toList());
        List<UserData> res = this.listByIds(dataIds);
        // 查询对应MySQL数据源信息
        LambdaQueryWrapper<DatasourceMetaInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DatasourceMetaInfo::getUserId, loginUser.getId());
        List<DatasourceMetaInfo> list = datasourceMetaInfoService.list(lqw);
        List<UserData> mysqlDataList = list.stream().map(this::datasourceMetaInfoToUserData).collect(Collectors.toList());
        res.addAll(mysqlDataList);
        if (dataIds.isEmpty()) {
            return new ArrayList<>();
        }
        return res;
    }

    public UserData datasourceMetaInfoToUserData(DatasourceMetaInfo datasourceMetaInfo) {
        UserData userData = new UserData();
        userData.setId(datasourceMetaInfo.getId());
        userData.setUserId(datasourceMetaInfo.getUserId());
        userData.setDataName(datasourceMetaInfo.getName());
        userData.setDescription(datasourceMetaInfo.getDescription());
        userData.setUploadType(1);
        userData.setCreateTime(datasourceMetaInfo.getCreateTime());
        userData.setUpdateTime(datasourceMetaInfo.getUpdateTime());
        return userData;
    }

    /**
     *
     * @param dataId
     * @param loginUser
     * @return
     */
    @Override
    public List<DataCollaboratorsVO> getDataCollaborators(Long dataId, User loginUser) {
        // 校验
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(dataId == null, ErrorCode.PARAMS_ERROR);
        // 查询对应dataId的数据协作者
        QueryWrapper<UserDataPermission> qw = new QueryWrapper<>();
        qw.select("userId", "permission");
        qw.eq("dataId", dataId);
        List<UserDataPermission> permissionList = userDataPermissionService.list(qw);
        // 去掉创建者本身
        List<UserDataPermission> userDataPermissionList = permissionList.stream()
                .filter(userDataPermission -> !userDataPermission.getUserId().equals(loginUser.getId()))
                .collect(Collectors.toList());
        // 查询对应协作者信息
        List<DataCollaboratorsVO> res = new ArrayList<>();
        userDataPermissionList.forEach(userDataPermission -> {
            User user = userService.getById(userDataPermission.getUserId());
            UserVO userVO = userService.getUserVO(user);
            DataCollaboratorsVO dataCollaboratorsVO = new DataCollaboratorsVO();
            dataCollaboratorsVO.setUserVO(userVO);
            dataCollaboratorsVO.setPermission(userDataPermission.getPermission());
            res.add(dataCollaboratorsVO);
        });
        return res;
    }

    @Override
    @Transactional
    public Long saveToMySQL(User loginUser, String dataName, String description, List<TableFieldInfo> tableFieldInfos, MultipartFile multipartFile) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(dataName), ErrorCode.NOT_LOGIN_ERROR);
        UserData userData = new UserData();
        userData.setUserId(loginUser.getId());
        userData.setDataName(dataName);
        userData.setDescription(description);
        userData.setUploadType(UserDataTypeEnum.MYSQL.getValue());
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
        boolean savePermission = userDataPermissionService.save(userDataPermission);
        ThrowUtils.throwIf(!savePermission, ErrorCode.SYSTEM_ERROR);
        // 将数据存储到MySQL中
        try {
            excelUtils.saveDataToMySQL(multipartFile.getInputStream(), userData.getId(), tableFieldInfos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 将数据存储到MongoDB中
        String tableFieldInfosString = JSON.toJSONString(tableFieldInfos);
        userData.setFieldTypeInfo(tableFieldInfosString);
        boolean update = this.updateById(userData);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        return userData.getId();
    }

    @Override
    public List<UserData> listMySQLByPermission(User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 获取当前用户具有权限的数据集ID
        QueryWrapper<UserDataPermission> qw = new QueryWrapper<>();
        qw.eq("userId", loginUser.getId());
        qw.select("dataId");
        // 查询对应的excel存储的地方
        // 显示MySQL数据集的信息
        List<Long> dataIds = userDataPermissionService.list(qw)
                .stream()
                .map(UserDataPermission::getDataId)
                .collect(Collectors.toList());
        QueryWrapper<UserData> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", dataIds).eq("uploadType", UserDataTypeEnum.MYSQL.getValue());
        return this.list(queryWrapper);
    }
}




