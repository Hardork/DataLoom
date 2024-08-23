package com.hwq.dataloom.config.job;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.entity.CoreDatasourceTask;
import com.hwq.dataloom.utils.ApiUtils;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.lang3.StringUtils;
import org.apache.groovy.util.BeanUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MyJobHandler {

    private static final String ADMIN_ADD_JOB_URL = "http://127.0.0.1:8088/xxl-job-admin/jobinfo/add";
    private static final String ADMIN_TRIGGER_JOB_URL = "http://127.0.0.1:8088/xxl-job-admin/jobinfo/trigger";

    @XxlJob("dataLoomJobHandler")
    public void DataLoomJobHandler() throws IOException, ParseException {
        String jobParam = XxlJobHelper.getJobParam();
        ApiDefinition apiDefinition = JSONUtil.toBean(jobParam, ApiDefinition.class);

        CloseableHttpResponse response = ApiUtils.getApiResponse(apiDefinition);
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);

    }

}