package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
     * 父级ID
     */
    private Long pid;

    /**
     * 更新方式：0：替换；1：追加
     */
    private String edit_type;

    /**
     * 详细信息
     */
    private String configuration;

    /**
     * 创建时间
     */
    private Long create_time;

    /**
     * 更新时间
     */
    private Long update_time;

    /**
     * 状态
     */
    private String status;

    /**
     * 任务状态
     */
    private String task_status;

    /**
     * 启用数据填报功能
     */
    private Integer enable_data_fill;

    /**
     * 创建的用户ID
     */
    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}