package com.hwq.dataloom.model.dto.newdatasource;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

@Data
public class TaskDTO implements Serializable {

    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * 更新方式 all_scope全量, add_scope增量
     */
    private String updateType;

    /**
     * 更新频率 CRON 表达式, RIGHTNOW 立即, SIMPLE_CRON 简单重复, MANUAL
     */
    private String syncRate;

    /**
     * 简单重复值 如 1 （minutes）
     */
    private Long simpleCronValue;

    /**
     * 简单重复单位 minutes hour day
     */
    private String simpleCronType;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;


    private String endLimit;

    /**
     * cron表达式
     */
    private String cron;

    private static final long serialVersionUID = 1175287571828910222L;
}
