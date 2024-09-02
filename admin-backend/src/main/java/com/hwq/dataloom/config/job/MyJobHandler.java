package com.hwq.dataloom.config.job;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.hwq.dataloom.constant.DatasourceConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.dto.newdatasource.TableField;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasourceTask;
import com.hwq.dataloom.model.enums.DataSourceTypeEnum;
import com.hwq.dataloom.service.CoreDatasetTableFieldService;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.CoreDatasourceTaskService;
import com.hwq.dataloom.utils.ApiUtils;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.*;

@Component
@Slf4j
public class MyJobHandler {

    @Resource
    private CoreDatasourceTaskService coreDatasourceTaskService;

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private CoreDatasetTableFieldService coreDatasetTableFieldService;

    @Resource
    private DatasourceEngine datasourceEngine;

    @Value("${xxl.job.admin.addresses:''}")
    private String adminAddresses;

    @Value("${xxl.job.admin.login-username:admin}")
    private String loginUsername;

    @Value("${xxl.job.admin.login-pwd:123456}")
    private String loginPwd;

    /**
     * 定时任务 根据API接口信息更新API数据
     * @throws IOException
     * @throws ParseException
     */
    @XxlJob("dataLoomJobHandler")
    public void DataLoomJobHandler() {
        String jobParam = XxlJobHelper.getJobParam();
        Date now = new Date();
        ApiDefinition apiDefinition = JSONUtil.toBean(jobParam, ApiDefinition.class);
        String tableName = apiDefinition.getDeTableName();
        QueryWrapper<CoreDatasetTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tableName", tableName);
        CoreDatasetTable datasetTable = coreDatasetTableService.getOne(queryWrapper);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(datasetTable), ErrorCode.PARAMS_ERROR, "数据表不存在");
        Long datasetTableId = datasetTable.getId();
        Long datasourceId = datasetTable.getDatasourceId();
        QueryWrapper<CoreDatasourceTask> taskQueryWrapper = new QueryWrapper<>();
        taskQueryWrapper.eq("datasetTableId", datasetTableId);
        CoreDatasourceTask coreDatasourceTask = coreDatasourceTaskService.getOne(taskQueryWrapper);
        try {
            CloseableHttpResponse response = ApiUtils.getApiResponse(apiDefinition);
            int code = response.getCode();
            if (code != 200) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "调用接口失败！错误码为：" + code);
            }
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println(responseBody);
            if (StringUtils.isEmpty(responseBody)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口调用失败！接口请求结果为空！");
            }
            coreDatasourceService.handleApiResponse(apiDefinition,responseBody);
            System.out.println(apiDefinition);
            // 执行并将字段更新到数据库中
            List<TableField> fields = apiDefinition.getFields();
            TableField uniqueField = new TableField();
            int columnIndex = 0;
            List<CoreDatasetTableField> coreDatasetTableFieldList = new ArrayList<>();
            String[] columns = new String[fields.size()];
            for (TableField field : fields) {
                columnIndex++;
                CoreDatasetTableField coreDatasetTableField = new CoreDatasetTableField();
                BeanUtil.copyProperties(field,coreDatasetTableField);
                coreDatasetTableField.setDatasetTableId(datasetTableId);
                coreDatasetTableField.setColumnIndex(columnIndex);
                coreDatasetTableField.setLastSyncTime(now.getTime());
                coreDatasetTableField.setGroupType("d");
                coreDatasetTableFieldList.add(coreDatasetTableField);
                // 获取唯一字段
                if (field.getIsUnique() == 1) {
                    uniqueField = field;
                }
                columns[columnIndex - 1] = field.getOriginName();
            }
            boolean savedBatch = coreDatasetTableFieldService.saveOrUpdateBatch(coreDatasetTableFieldList);
            ThrowUtils.throwIf(!savedBatch,ErrorCode.OPERATION_ERROR,"新增字段失败！");
            // 更新数据仓库
            String updateType = coreDatasourceTask.getUpdateType();
            if (updateType.equals("all_scope")) {
                // 全量更新
                log.info("执行全量更新" + String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.API.getValue(), datasourceId, apiDefinition.getName()));
                datasourceEngine.exeDropTable(datasourceId,String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.API.getValue(), datasourceId, apiDefinition.getName()));
                datasourceEngine.exeCreateTable(datasourceId,String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.API.getValue(), datasourceId, apiDefinition.getName()),coreDatasetTableFieldList);
                List<String[]> dataList = ApiUtils.toDataList(JSONUtil.toJsonStr(apiDefinition));
                int pageNumber = 1000; //一次插入 1000条
                int totalPage;
                if (dataList.size() % pageNumber > 0) {
                    totalPage = dataList.size() / pageNumber + 1;
                } else {
                    totalPage = dataList.size() / pageNumber;
                }
                for (int page = 1; page <= totalPage; page++) {
                    datasourceEngine.execInsert(datasourceId, String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.API.getValue(), datasourceId, apiDefinition.getName()), dataList, page, pageNumber);
                }
            } else {
                // 增量更新
                log.info("执行增量更新" + String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.API.getValue(), datasourceId, apiDefinition.getName()));
                ThrowUtils.throwIf(ObjectUtils.isEmpty(uniqueField),ErrorCode.PARAMS_ERROR,"执行增量更新失败，未设置唯一字段！");
                // 获取增量数据
                List<String[]> dataList = ApiUtils.toDataList(JSONUtil.toJsonStr(apiDefinition));
                int pageNumber = 1000; //一次插入 1000条
                int totalPage;
                if (dataList.size() % pageNumber > 0) {
                    totalPage = dataList.size() / pageNumber + 1;
                } else {
                    totalPage = dataList.size() / pageNumber;
                }
                for (int page = 1; page <= totalPage; page++) {
                    // 添加增量数据进入数据仓库
                    datasourceEngine.execInsertAndUpdate(datasourceId,String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.API.getValue(), datasourceId, apiDefinition.getName()),dataList,page,pageNumber,columns,uniqueField);
                }
            }
        } catch (Exception e) {
            // 更新任务信息
            coreDatasourceTask.setLastExecTime(now.getTime());
            coreDatasourceTask.setLastExecStatus("failed");
            coreDatasourceTaskService.updateById(coreDatasourceTask);
        }
        // 更新任务信息
        coreDatasourceTask.setLastExecTime(now.getTime());
        coreDatasourceTask.setLastExecStatus("succeed");
        coreDatasourceTaskService.updateById(coreDatasourceTask);

    }

    /**
     * 清除过期的定时任务 需要手动注册
     * @throws IOException
     * @throws ParseException
     */
    @XxlJob("cleanUpExpiredJobs")
    public void cleanUpExpiredJobs() throws IOException, ParseException {
        List<CoreDatasourceTask> list = coreDatasourceTaskService.list();
        Date now = new Date();
        for (CoreDatasourceTask task : list) {
            if (task.getEndTime() != null && now.after(task.getEndTime())) {
                // 停止并删除任务
                log.info("Job [{}] has expired, stopping and deleting it", task.getId());
                int xxlJobId = coreDatasourceTaskService.getById(task.getId()).getJobId();
                coreDatasourceTaskService.removeById(task.getId());
                try {
                    XxlJobUtil.login(adminAddresses,loginUsername,loginPwd);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                JSONObject response = XxlJobUtil.stopJob(adminAddresses, xxlJobId);
                if (response.containsKey("code") && 200 == (Integer) response.get("code")) {
                    System.out.println("任务停止成功");
                } else {
                    System.out.println("任务停止失败");
                }
            }
        }
    }





}