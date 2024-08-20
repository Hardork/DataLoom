package com.hwq.dataloom.model.dto.newdatasource;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.io.*;

@Data
public class DatasourceDTO implements Serializable {

    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using= ToStringSerializer.class)
    private Long pid;

    /**
     * 数据源名称
     */
    @NotNull(message = "数据源名称不得为空")
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型
     */
    @NotNull(message = "数据源类型不得为空")
    private String type;

    private String typeAlias;

    private String catalog;

    private String catalogDesc;

    /**
     * 数据表详细信息
     */
    @NotNull(message = "数据源配置信息不得为空")
    private String configuration;

    private String apiConfigurationStr;
    private String paramsStr;

    /**
     * Create timestamp
     */
    private Long createTime;

    /**
     * Update timestamp
     */
    private Long updateTime;

    /**
     * 创建人ID
     */
    private String userId;

    /**
     * 接口状态
     */
    private String status;

    /**
     *  同步任务设置
     */
    private TaskDTO syncSetting;

    private Integer editType;
    private String  nodeType;
    private String  action;
    private String  fileName;
    private String  size;
    /**
     * 上次成功更新时间
     */
    private Long lastSyncTime;

    /**
     * 任务状态TaskDTO
     */
    private String taskStatus;

    /**
     * 是否启动数据填报功能
     */
    private Boolean enableDataFill;

    private static final long serialVersionUID = 1175287571828910222L;
}
