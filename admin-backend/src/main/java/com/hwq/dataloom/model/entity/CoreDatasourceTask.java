package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 数据源定时同步任务
 * @TableName core_datasource_task
 */
@TableName(value ="core_datasource_task")
@Data
public class CoreDatasourceTask implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 数据源ID
     */
    private Long ds_id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 更新方式
     */
    private String update_type;

    /**
     * 开始时间
     */
    private Long start_time;

    /**
     * 执行频率：0 一次性 1 cron
     */
    private String sync_rate;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 简单重复间隔
     */
    private Long simple_cron_value;

    /**
     * 简单重复类型：分、时、天
     */
    private String simple_cron_type;

    /**
     * 结束限制 0 无限制 1 设定结束时间
     */
    private String end_limit;

    /**
     * 结束时间
     */
    private Long end_time;

    /**
     * 创建时间
     */
    private Long create_time;

    /**
     * 上次执行时间
     */
    private Long last_exec_time;

    /**
     * 上次执行结果
     */
    private String last_exec_status;

    /**
     * 额外数据
     */
    private String extra_data;

    /**
     * 任务状态
     */
    private String task_status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}