package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 数据源表
 * @TableName core_datasource
 */
@TableName(value ="core_datasource")
@Data
public class CoreDatasource implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型
     */
    private String type;

    /**
     * 父级ID --文件夹
     */
    private Long pid;

    /**
     * 更新方式：0：替换；1：追加
     */
    private String editType;

    /**
     * 详细信息
     */
    private String configuration;

    /**
     * 状态
     */
    private String status;

    /**
     * 任务状态
     */
    private String taskStatus;

    /**
     * 启用数据填报功能 0-不填报 1-填报
     */
    private Integer enableDataFill;

    /**
     * 创建的用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}