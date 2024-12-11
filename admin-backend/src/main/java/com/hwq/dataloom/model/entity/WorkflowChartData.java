package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/12/10
 * @Description:
 **/
@TableName(value ="workflow_chart_data")
@Data
public class WorkflowChartData {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 工作流图表ID
     */
    @TableField(value = "workflowChartId")
    private Long workflowChartId;

    /**
     * 图表数据
     */
    @TableField(value = "value")
    private Long value;

    /**
     * 数据收集时间
     */
    @TableField(value = "collectionTime")
    private Date collectionTime;

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


}
