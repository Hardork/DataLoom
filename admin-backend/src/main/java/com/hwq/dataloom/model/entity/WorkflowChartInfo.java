package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/12/10
 * @Description:
 **/
@TableName(value ="workflow_chart_info")
@Data
public class WorkflowChartInfo {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 工作流图表名称
     */
    @TableField(value = "chartName")
    private String chartName;

    /**
     * 图表描述
     */
    @TableField(value = "chartDesc")
    private String chartDesc;

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
