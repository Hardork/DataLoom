package com.hwq.dataloom.controller;

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
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hwq.dataloom.utils.ApiUtils.handleStr;

/**
 * 新数据源接口
 */
@RestController
@RequestMapping("/coreDatasource")
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
    public BaseResponse<Long> addDatasource(@RequestBody DatasourceDTO datasourceDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(datasourceDTO == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 根据不同类型configuration新增表 （用策略模式优化）
        return ResultUtils.success(coreDatasourceService.addDatasource(datasourceDTO, loginUser));
    }


    /**
     * 校验数据源
     * @param datasourceDTO
     * @return
     */
    @PostMapping("/check")
    public BaseResponse<Boolean> checkDatasource(@RequestBody DatasourceDTO datasourceDTO) {
        ThrowUtils.throwIf(datasourceDTO == null, ErrorCode.PARAMS_ERROR);
        // 根据不同类型configuration校验表 （用策略模式优化）
        return ResultUtils.success(coreDatasourceService.validDatasourceConfiguration(datasourceDTO));
    }


    @GetMapping("/getTables")
    public BaseResponse<List<CoreDatasetTable>> getTablesByDatasourceId(Long datasourceId, HttpServletRequest request) {
        ThrowUtils.throwIf(datasourceId == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 根据不同数据源类型
        return ResultUtils.success(coreDatasourceService.getTablesByDatasourceId(datasourceId, loginUser));
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
