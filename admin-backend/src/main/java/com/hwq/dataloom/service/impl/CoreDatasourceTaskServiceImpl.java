package com.hwq.dataloom.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.config.job.XxlJobInfo;
import com.hwq.dataloom.config.job.XxlJobUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.mapper.CoreDatasourceTaskMapper;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.dto.newdatasource.TaskDTO;
import com.hwq.dataloom.model.entity.CoreDatasourceTask;
import com.hwq.dataloom.service.CoreDatasourceTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

@Service
@Slf4j
public class CoreDatasourceTaskServiceImpl extends ServiceImpl<CoreDatasourceTaskMapper, CoreDatasourceTask>
    implements CoreDatasourceTaskService {

    @Value("${xxl.job.admin.addresses:''}")
    private String adminAddresses;

    @Value("${xxl.job.admin.login-username:admin}")
    private String loginUsername;

    @Value("${xxl.job.admin.login-pwd:123456}")
    private String loginPwd;

    // XXL-JOB登录
    public void xxljob_login()
    {
        try {
            XxlJobUtil.login(adminAddresses,loginUsername,loginPwd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Resource
    @Lazy
    private CoreDatasourceTaskService coreDatasourceTaskService;

    @Override
    public Long addTask(DatasourceDTO datasourceDTO, Long datasetTableId, Integer xxlJobId) {
        CoreDatasourceTask coreDatasourceTask = new CoreDatasourceTask();
        coreDatasourceTask.setDatasourceId(datasourceDTO.getId());
        TaskDTO taskDTO = datasourceDTO.getSyncSetting();
        ThrowUtils.throwIf(taskDTO == null, ErrorCode.PARAMS_ERROR);
        coreDatasourceTask.setName(datasourceDTO.getId() + "同步任务");
        coreDatasourceTask.setDatasetTableId(datasetTableId);
        coreDatasourceTask.setUpdateType(taskDTO.getUpdateType());
        coreDatasourceTask.setStartTime(taskDTO.getStartTime());
        coreDatasourceTask.setSyncRate(taskDTO.getSyncRate());
        coreDatasourceTask.setCron(taskDTO.getCron());
        coreDatasourceTask.setSimpleCronValue(taskDTO.getSimpleCronValue());
        coreDatasourceTask.setSimpleCronType(taskDTO.getSimpleCronType());
        coreDatasourceTask.setEndLimit(taskDTO.getEndLimit());
        coreDatasourceTask.setEndTime(taskDTO.getEndTime());
        coreDatasourceTask.setTaskStatus("执行中");
        coreDatasourceTask.setJobId(xxlJobId);

        boolean save = coreDatasourceTaskService.save(coreDatasourceTask);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        Long id = coreDatasourceTask.getId();

        return id;
    }

    @Override
    public int addXxlJob(DatasourceDTO datasourceDTO, ApiDefinition apiDefinition) {
        TaskDTO taskDTO = datasourceDTO.getSyncSetting();
        // 创建并执行定时任务
        XxlJobInfo xxlJobInfo=new XxlJobInfo();
        // 此处JobGroup实际是XXLJOB的执行器id 需要到数据库中获取
        xxlJobInfo.setJobGroup(3);
        xxlJobInfo.setJobDesc(datasourceDTO.getId() + "同步任务");
        xxlJobInfo.setJobCron(taskDTO.getCron());
        xxlJobInfo.setAddTime(new Date());
        xxlJobInfo.setUpdateTime(new Date());
        xxlJobInfo.setAuthor("admin");
        xxlJobInfo.setAlarmEmail("2502022483@qq.com");
        if (!StringUtils.equals(taskDTO.getUpdateType(), "RIGHTNOW")) {
            xxlJobInfo.setScheduleType("CRON");
        }
        if (StringUtils.isNotEmpty(taskDTO.getCron())) {
            xxlJobInfo.setScheduleConf(taskDTO.getCron());
        }
        xxlJobInfo.setMisfireStrategy("DO_NOTHING");
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorHandler("dataLoomJobHandler");
        xxlJobInfo.setExecutorParam(JSONUtil.toJsonStr(apiDefinition));
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(1);
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setGlueSource("");
        xxlJobInfo.setGlueRemark("GLUE代码初始化");
        xxlJobInfo.setGlueUpdatetime(new Date());

        JSONObject test = (JSONObject) JSONObject.toJSON(xxlJobInfo);

        xxljob_login();

        try {
            JSONObject response = XxlJobUtil.addJob(adminAddresses, test);
            // 如果此处报错可能是以下三个原因
            // 1. xxljob执行器存在问题（检查上面代码中setJobGroup是否正确 检查配置文件中appName是否正确 检查控制台有没有新增执行器）
            // 2. xxljob源码中没有添加自定义restful接口
            // 3. xxljob数据库中xxl_job_info和xxl_job_log的executor_param字段没有改为text类型
            if (response.containsKey("code") && 200 == (Integer) response.get("code")) {
                String jobId = response.getString("content");
                log.info("新增成功,jobId:" + jobId);
                xxlJobInfo.setId(Integer.parseInt(jobId));
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"新增任务失败！");
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return xxlJobInfo.getId();
    }


    @Override
    public int deleteXxlJob(Integer jobId) {
        xxljob_login();
        JSONObject response = null;
        try {
            response = XxlJobUtil.deleteJob(adminAddresses, jobId);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        if (response.containsKey("code") && 200 == (Integer) response.get("code")) {
            return jobId;
        } else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"删除任务失败！");
        }
    }

}
