package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 图表配置表
 * @TableName chart_option
 */
@TableName(value ="chart_option")
@Data
public class ChartOption implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 对应仪表盘id
     */
    private Long dashboardId;

    /**
     * 图表名称
     */
    private String chartName;

    /**
     * 图表配置
     */
    private String chartOption;


    /**
     * 数据配置
     */
    private String dataOption;

    /**
     * 图表配置对应的自定义sql
     */
    private String customSql;

    /**
     * 图表智能分析结果
     */
    private String analysisRes;

    /**
     * 图表更新状态
     */
    private Boolean analysisLastFlag;

    /**
     * 图表状态
     */
    private Integer status;

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