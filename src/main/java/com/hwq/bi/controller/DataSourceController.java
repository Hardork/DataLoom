package com.hwq.bi.controller;

import com.hwq.bi.common.BaseResponse;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.common.ResultUtils;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.dto.datasource.DataSourceConfig;
import com.hwq.bi.model.dto.datasource.PreviewData;
import com.hwq.bi.model.dto.datasource.PreviewDataRequest;
import com.hwq.bi.model.entity.DatasourceMetaInfo;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.service.DatasourceMetaInfoService;
import com.hwq.bi.service.UserService;
import com.hwq.bi.utils.datasource.DruidUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
    @Resource
    private DatasourceMetaInfoService datasourceMetaInfoService;

    /**
     * 检验连接
     * @param dataSourceConfig
     * @param request
     * @return
     */
    @PostMapping("/checkValid")
    public BaseResponse<Boolean> checkConnect(@RequestBody @Valid DataSourceConfig dataSourceConfig, HttpServletRequest request) {
        // 校验参数
        validDataSourceConfig(dataSourceConfig);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return ResultUtils.success(DruidUtil.checkConnectValid(dataSourceConfig));
    }

    /**
     * 显示MySQL连接数据
     * @param previewDataRequest
     * @param request
     * @return
     */
    @PostMapping("/previewData")
    public BaseResponse<PreviewData> previewData(@RequestBody PreviewDataRequest previewDataRequest, HttpServletRequest request) {
        // 校验参数
        ThrowUtils.throwIf(previewDataRequest == null, ErrorCode.PARAMS_ERROR);
        String datasourceId = previewDataRequest.getDatasourceId();
        String dataName = previewDataRequest.getDataName();
        ThrowUtils.throwIf(datasourceId == null, ErrorCode.PARAMS_ERROR, "datasourceId不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(dataName), ErrorCode.PARAMS_ERROR, "dataName");
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        DatasourceMetaInfo datasourceMetaInfo = datasourceMetaInfoService.getById(datasourceId);
        ThrowUtils.throwIf(datasourceMetaInfo == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!loginUser.getId().equals(datasourceMetaInfo.getUserId()), ErrorCode.NO_AUTH_ERROR);
        return ResultUtils.success(datasourceMetaInfoService.PreviewData(previewDataRequest, datasourceMetaInfo));
    }

    /**
     * 保存数据源信息
     * @param dataSourceConfig
     * @param request
     * @return
     */
    @PostMapping("/save")
    public BaseResponse<Boolean> saveDataSourceMetaInfo(@RequestBody @Valid DataSourceConfig dataSourceConfig, HttpServletRequest request) {
        // 校验参数
        validDataSourceConfig(dataSourceConfig);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return ResultUtils.success(datasourceMetaInfoService.saveDataSourceMetaInfo(dataSourceConfig, loginUser));
    }

    /**
     * 校验数据
     * @param dataSourceConfig
     */
    public void validDataSourceConfig(DataSourceConfig dataSourceConfig) {
        ThrowUtils.throwIf(dataSourceConfig == null, ErrorCode.PARAMS_ERROR);
        String name = dataSourceConfig.getName();
        String host = dataSourceConfig.getHost();
        String port = dataSourceConfig.getPort();
        String dataBaseName = dataSourceConfig.getDataBaseName();
        String userName = dataSourceConfig.getUserName();
        String password = dataSourceConfig.getPassword();
        ThrowUtils.throwIf(StringUtils.isEmpty(name), ErrorCode.PARAMS_ERROR, "name不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(host), ErrorCode.PARAMS_ERROR, "host不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(port), ErrorCode.PARAMS_ERROR, "port不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(dataBaseName), ErrorCode.PARAMS_ERROR, "dataBaseName不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(userName), ErrorCode.PARAMS_ERROR, "userName不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(password), ErrorCode.PARAMS_ERROR, "password不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(password), ErrorCode.PARAMS_ERROR, "password不得为空");
    }
}
