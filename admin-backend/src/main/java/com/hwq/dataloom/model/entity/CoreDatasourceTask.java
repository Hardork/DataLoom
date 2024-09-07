package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import org.joda.time.DateTime;

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
    private Long datasourceId;

    /**
     * 数据表ID
     */
    private Long datasetTableId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 更新方式
     */
    private String updateType;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 执行频率：0 一次性 1 cron
     */
    private String syncRate;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 简单重复间隔
     */
    private Long simpleCronValue;

    /**
     * 简单重复类型：分、时、天
     */
    private String simpleCronType;

    /**
     * 结束限制 0 无限制 1 设定结束时间
     */
    private String endLimit;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 上次执行时间
     */
    private Long lastExecTime;

    /**
     * 上次执行结果
     */
    private String lastExecStatus;

    /**
     * 额外数据
     */
    private String extraData;

    /**
     * 任务状态
     */
    private String taskStatus;

    /**
     * xxljob定时任务ID
     */
    private Integer jobId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}