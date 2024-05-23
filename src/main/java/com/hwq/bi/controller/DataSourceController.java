package com.hwq.bi.controller;

import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.dto.datasource.DataSourceConfig;
import com.hwq.bi.model.dto.datasource.PreviewData;
import com.hwq.bi.model.dto.datasource.PreviewDataRequest;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.service.UserService;
import com.hwq.bi.utils.datasource.DruidUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author HWQ
 * @date 2024/5/24 01:28
 * @description
 */
@RestController
@RequestMapping("/datasource")
public class DataSourceController {
    @Resource
    private UserService userService;
    @PostMapping("/checkValid")
    public Boolean checkConnect(@RequestBody DataSourceConfig dataSourceConfig, HttpServletRequest request) {
        // 校验参数
        validDataSourceConfig(dataSourceConfig);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return DruidUtil.checkConnectValid(dataSourceConfig);
    }

//    @PostMapping("/previewData")
//    public PreviewData previewData(@RequestBody PreviewDataRequest previewDataRequest, HttpServletRequest request) {
//        // 校验参数
//        validDataSourceConfig(dataSourceConfig);
//        User loginUser = userService.getLoginUser(request);
//        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
//        return DruidUtil.getPreviewData(dataSourceConfig);
//    }

    public void validDataSourceConfig(DataSourceConfig dataSourceConfig) {
        ThrowUtils.throwIf(dataSourceConfig == null, ErrorCode.PARAMS_ERROR);
        String name = dataSourceConfig.getName();
        String type = dataSourceConfig.getType();
        String host = dataSourceConfig.getHost();
        String port = dataSourceConfig.getPort();
        String dataBaseName = dataSourceConfig.getDataBaseName();
        String userName = dataSourceConfig.getUserName();
        String password = dataSourceConfig.getPassword();
        ThrowUtils.throwIf(StringUtils.isEmpty(type), ErrorCode.PARAMS_ERROR, "type不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(name), ErrorCode.PARAMS_ERROR, "name不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(host), ErrorCode.PARAMS_ERROR, "host不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(port), ErrorCode.PARAMS_ERROR, "port不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(dataBaseName), ErrorCode.PARAMS_ERROR, "dataBaseName不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(userName), ErrorCode.PARAMS_ERROR, "userName不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(password), ErrorCode.PARAMS_ERROR, "password不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(password), ErrorCode.PARAMS_ERROR, "password不得为空");
    }
}
