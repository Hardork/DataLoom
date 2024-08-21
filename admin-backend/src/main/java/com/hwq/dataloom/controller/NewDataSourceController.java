package com.hwq.dataloom.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.common.http.HttpClientConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinitionRequest;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.CoreDatasourceTaskService;
import com.hwq.dataloom.service.UserService;
import com.hwq.dataloom.utils.strategy.DataSourceStrategyManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.hwq.dataloom.utils.ApiUtils.handleStr;

/**
 * 新数据源接口
 */
@RestController
@RequestMapping("/newdatasource")
@Slf4j
public class NewDataSourceController {

    @Resource
    private UserService userService;

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private CoreDatasourceTaskService coreDatasourceTaskService;

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @PostMapping("/folder")
    public BaseResponse<Long> addFolder(Long pid, String name, HttpServletRequest request) {
        if (name == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请添加文件夹名称");
        }
        CoreDatasource coreDatasource = new CoreDatasource();
        coreDatasource.setName(name);
        coreDatasource.setType("folder");
        coreDatasource.setPid(pid);
        User loginUser = userService.getLoginUser(request);
        coreDatasource.setUserId(loginUser.getId());
        boolean save = coreDatasourceService.save(coreDatasource);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        Long id = coreDatasource.getId();
        return ResultUtils.success(id);
    }

    @PostMapping("/datasource")
    public BaseResponse<Long> addDatasource(@RequestBody DatasourceDTO datasourceDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(datasourceDTO == null, ErrorCode.PARAMS_ERROR);
        // 新增数据源
        CoreDatasource coreDatasource = new CoreDatasource();
        coreDatasource.setName(datasourceDTO.getName());
        coreDatasource.setDescription(datasourceDTO.getDescription());
        coreDatasource.setType(datasourceDTO.getType());
        coreDatasource.setPid(datasourceDTO.getPid());
        coreDatasource.setEditType(datasourceDTO.getEditType().toString());
        coreDatasource.setConfiguration(datasourceDTO.getConfiguration());
        coreDatasource.setStatus(datasourceDTO.getStatus());
        coreDatasource.setTaskStatus(datasourceDTO.getTaskStatus());
        coreDatasource.setEnableDataFill(coreDatasource.getEnableDataFill());
        User loginUser = userService.getLoginUser(request);
        coreDatasource.setUserId(loginUser.getId());
        boolean save = coreDatasourceService.save(coreDatasource);
        ThrowUtils.throwIf(!save,ErrorCode.OPERATION_ERROR,"新增数据源失败！");
        Long id = coreDatasource.getId();
        // 根据不同类型configuration新增表 （用策略模式优化）
        DataSourceStrategyManager dataSourceStrategyManager = new DataSourceStrategyManager();
        dataSourceStrategyManager.handleConfiguration(coreDatasource,datasourceDTO);
        return ResultUtils.success(id);
    }

    @PostMapping("/checkApiDatasource")
    public BaseResponse<ApiDefinition> checkApiDatasource(@RequestBody ApiDefinition apiDefinition) throws IOException, ParseException {
        ThrowUtils.throwIf(apiDefinition == null, ErrorCode.PARAMS_ERROR, "请求为空");
        ApiDefinitionRequest apiDefinitionRequest = apiDefinition.getRequest();

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpUriRequestBase request = null;

        // 根据请求方法创建对应的请求对象
        switch (apiDefinition.getMethod().toUpperCase()) {
            case "POST":
                request = new HttpPost(apiDefinition.getUrl());
                break;
            case "GET":
                // 构建 GET 请求的URL
                String url = apiDefinition.getUrl();
                if(!apiDefinitionRequest.getArguments().isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder(url);
                    stringBuilder.append("?");
                    for (Map<String, String> argument : apiDefinitionRequest.getArguments()) {
                        for (Map.Entry<String, String> entry : argument.entrySet()) {
                            stringBuilder.append(entry.getKey())
                                    .append("=")
                                    .append(entry.getValue())
                                    .append("&");
                        }
                    }
                    url = stringBuilder.toString().replaceAll("&$", "");
                }
                request = new HttpGet(url);
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(30000))
                .setResponseTimeout(Timeout.ofSeconds(apiDefinition.getApiQueryTimeout() * 1000))
                .build();
        request.setConfig(requestConfig);

        // 设置请求头
        for (Map<String, String> header : apiDefinition.getRequest().getHeaders()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 设置请求体
        if (apiDefinition.getMethod().equalsIgnoreCase("POST")) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(apiDefinition.getRequest().getBody());
            ((HttpPost) request).setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        }

        // 获取结果
        CloseableHttpResponse response = httpClient.execute(request);
        int code = response.getCode();
        if (code != 200) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"调用接口失败！");
        }
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);

        // 处理结果
        List<Map<String,Object>> fields = new ArrayList<>();
        String rootPath = "";
        try {
            handleStr(apiDefinition,responseBody,fields,rootPath);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"调用接口失败！");
        }
        apiDefinition.setJsonFields(fields);

        // TODO 修改同步任务的最新同步时间

        return ResultUtils.success(apiDefinition);
    }

}
