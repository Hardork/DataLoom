package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName workflow_runs
 */
@TableName(value ="workflow_runs")
@Data
public class WorkflowRuns implements Serializable {
    /**
     * 自增长主键，对应类中的id属性
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，对应类中的userId属性
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 自增序号，对应类中的sequenceNumber属性
     */
    @TableField(value = "sequenceNumber")
    private Integer sequenceNumber;

    /**
     * 工作流ID，对应类中的workflowId属性
     */
    @TableField(value = "workflowId")
    private Long workflowId;

    /**
     * 工作流类型，对应类中的type属性
     */
    @TableField(value = "type")
    private String type;

    /**
     * 触发来源，对应类中的triggeredFrom属性
     */
    @TableField(value = "triggeredFrom")
    private String triggeredFrom;

    /**
     * 版本，对应类中的version属性
     */
    @TableField(value = "version")
    private String version;

    /**
     * 工作流画布配置（JSON格式），对应类中的graph属性
     */
    @TableField(value = "graph")
    private String graph;

    /**
     * 输入参数，对应类中的inputs属性
     */
    @TableField(value = "inputs")
    private String inputs;

    /**
     * 执行状态，对应类中的status属性
     */
    @TableField(value = "status")
    private String status;

    /**
     * 输出内容，对应类中的outputs属性
     */
    @TableField(value = "outputs")
    private String outputs;

    /**
     * 错误原因，对应类中的error属性
     */
    @TableField(value = "error")
    private String error;

    /**
     * 耗时（秒），对应类中的elapsedTime属性
     */
    @TableField(value = "elapsedTime")
    private Double elapsedTime;

    /**
     * 总使用的token数量，对应类中的totalTokens属性
     */
    @TableField(value = "totalTokens")
    private Integer totalTokens;

    /**
     * 总步骤数，对应类中的totalSteps属性
     */
    @TableField(value = "totalSteps")
    private Integer totalSteps;

    /**
     * 创建者角色，对应类中的createdByRole属性
     */
    @TableField(value = "createRole")
    private String createRole;

    /**
     * 结束时间
     */
    @TableField(value = "finishedTime")
    private LocalDateTime finishedTime;

    /**
     * 创建时间，对应类中的createTime属性
     */
    @TableField(value = "createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间，对应类中的updateTime属性
     */
    @TableField(value = "updateTime")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识，对应类中的isDelete属性
     */
    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}