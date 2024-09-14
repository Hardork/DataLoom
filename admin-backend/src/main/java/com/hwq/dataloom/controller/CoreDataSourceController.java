package com.hwq.dataloom.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.datasource.GetTableFieldsDTO;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.UserService;
import com.hwq.dataloom.utils.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 新数据源接口
 */
@RestController
@RequestMapping("/admin/coreDatasource")
@Slf4j
public class CoreDataSourceController {

    @Resource
    private UserService userService;

    @Resource
    private CoreDatasourceService coreDatasourceService;


    /**
     * 添加数据源
     * @param datasourceDTO
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addDatasource(@RequestPart("file") MultipartFile multipartFile, @RequestPart("datasourceDTO") DatasourceDTO datasourceDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(datasourceDTO == null, ErrorCode.PARAMS_ERROR);
        datasourceDTO.setMultipartFile(multipartFile);
        User loginUser = userService.getLoginUser(request);
        // 根据不同类型configuration新增表 （用策略模式优化）
        return ResultUtils.success(coreDatasourceService.addDatasource(datasourceDTO, loginUser));
    }

    /**
     * 根据数据源id获取数据源信息
     * @param datasourceId
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<DatasourceDTO> getDataSource(Long datasourceId, HttpServletRequest request) {
        ThrowUtils.throwIf(datasourceId == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(coreDatasourceService.getDataSource(datasourceId, loginUser));
    }

    @Operation(summary = "校验数据源")
    @PostMapping("/check")
    public BaseResponse<Boolean> checkDatasource(@RequestBody DatasourceDTO datasourceDTO) {
        ThrowUtils.throwIf(datasourceDTO == null, ErrorCode.PARAMS_ERROR);
        // 根据不同类型configuration校验表 （用策略模式优化）
        return ResultUtils.success(coreDatasourceService.validDatasourceConfiguration(datasourceDTO));
    }

    @Operation(summary = "展示用户数据源列表")
    @GetMapping("/list")
    public BaseResponse<List<CoreDatasource>> listUserDataSource(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        LambdaQueryWrapper<CoreDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoreDatasource::getUserId, loginUser.getId());
        return ResultUtils.success(coreDatasourceService.list(wrapper));
    }

    
    @Operation(summary = "获取数据源所有表信息")
    @GetMapping("/getTables")
    public BaseResponse<List<CoreDatasetTable>> getTablesByDatasourceId(Long datasourceId, HttpServletRequest request) {
        ThrowUtils.throwIf(datasourceId == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 根据不同数据源类型
        return ResultUtils.success(coreDatasourceService.getTablesByDatasourceId(datasourceId, loginUser));
    }

    /**
     * 获取数据源指定表所有字段信息
     * @param getTableFieldsDTO
     * @param request
     * @return
     */
    @GetMapping("/getTableFields")
    public BaseResponse<List<CoreDatasetTableField>> getTableFieldsByDatasourceIdAndTableName(@RequestBody @Valid GetTableFieldsDTO getTableFieldsDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(getTableFieldsDTO == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 根据不同数据源类型
        return ResultUtils.success(coreDatasourceService.getTableFieldsByDatasourceIdAndTableName(getTableFieldsDTO, loginUser));
    }

    /**
     * 处理API返回结果
     * @param apiDefinition
     * @return
     * @throws IOException
     * @throws ParseException
     */
    @PostMapping("/handleApiResponse")
    public BaseResponse<ApiDefinition> handleApiResponse(@RequestBody ApiDefinition apiDefinition) throws IOException, ParseException {
        if (apiDefinition.getFields() == null) {
            apiDefinition.setFields(new ArrayList<>());
        }
        // 向API发送请求
        CloseableHttpResponse response = ApiUtils.getApiResponse(apiDefinition);
        String responseBody = null;
        int code = response.getCode();
        if (code != 200) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "调用接口失败！错误码为：" + code);
        }
        responseBody = EntityUtils.toString(response.getEntity());
        if (StringUtils.isEmpty(responseBody)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口调用失败！接口请求结果为空！");
        }
        // 处理结果
        coreDatasourceService.handleApiResponse(apiDefinition,responseBody);

        return ResultUtils.success(apiDefinition);
    }

}
