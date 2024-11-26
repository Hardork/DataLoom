package com.hwq.dataloom.core.ops.entitys;

import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.model.entity.WorkflowRuns;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: 工作流跟踪信息类
 * @DateTime: 2024/11/26 11:02
 **/
@Data
public class WorkflowTraceInfo extends BaseTraceInfo {
    private Object workflowData;
    private Long workflowId;
    private Long conversationId;
    private Long userId;
    private Long workflowRunId;
    private double workflowRunElapsedTime;
    private String workflowRunStatus;
    private Map<String, Object> workflowRunInputs;
    private Map<String, Object> workflowRunOutputs;
    private String workflowRunVersion;
    private String error;
    private int totalTokens;
    private List<String> fileList;
    private String query;

    public WorkflowTraceInfo(WorkflowRuns workflowRun, Long conversationId, Long userId) {
        this.workflowRunId = workflowRun.getId();
        this.conversationId = conversationId;
        this.userId = userId;
        this.workflowId = workflowRun.getWorkflowId();
        this.workflowRunElapsedTime = workflowRun.getElapsedTime();
        this.workflowRunStatus = workflowRun.getStatus();
        this.workflowRunInputs = JSONUtil.toBean(workflowRun.getInputs(), Map.class);
        this.workflowRunOutputs = JSONUtil.toBean(workflowRun.getOutputs(), Map.class);
        this.workflowRunVersion = workflowRun.getVersion();
        this.error = workflowRun.getError();
        this.totalTokens = workflowRun.getTotalTokens();
        this.fileList = JSONUtil.toBean(workflowRun.getInputs(), List.class);
        this.query = workflowRunInputs.get("query").toString();

        Map<String, Object> metaData = new HashMap<>();
        metaData.put("conversationId", conversationId);
        metaData.put("userId", userId);
        metaData.put("workflowId", workflowId);
        metaData.put("workflowRunElapsedTime", workflowRunElapsedTime);
        metaData.put("workflowRunStatus", workflowRunStatus);
        metaData.put("workflowRunVersion", workflowRunVersion);
        metaData.put("error", error);
        metaData.put("totalTokens", totalTokens);
        metaData.put("fileList", fileList);
        metaData.put("query", query);

        this.setMetadata(metaData);
        this.setStartTime(workflowRun.getCreateTime());
        this.setEndTime(workflowRun);
    }
}
