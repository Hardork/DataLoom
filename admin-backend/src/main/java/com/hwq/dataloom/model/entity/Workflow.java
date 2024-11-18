package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 工作流表
 * @TableName workflow
 */
@TableName(value ="workflow")
@Data
public class Workflow implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "workflowId", type = IdType.AUTO)
    private Long workflowId;

    /**
     * 工作流名称
     */
    @TableField(value = "workflowName")
    private String workflowName;

    /**
     * 工作流图标
     */
    @TableField(value = "workflowIcon")
    private String workflowIcon;

    /**
     * 工作流作用描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 创建用户ID
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 工作流类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 版本信息
     */
    @TableField(value = "version")
    private String version;

    /**
     * 画布配置（JSON格式）
     */
    @TableField(value = "graph")
    private String graph;

    /**
     * 功能特性相关数据（JSON格式）
     */
    @TableField(value = "features")
    private String features;

    /**
     * 环境变量（JSON格式）
     */
    @TableField(value = "envVariables")
    private String envVariables;

    /**
     * 对话变量（JSON格式）
     */
    @TableField(value = "conversationVariables")
    private String conversationVariables;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}