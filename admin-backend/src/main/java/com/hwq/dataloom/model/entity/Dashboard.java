package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 仪表盘表
 * @TableName dashboard
 */
@TableName(value ="dashboard")
@Data
public class Dashboard implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 仪表盘名称
     */
    private String name;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 数据源ID
     */
    private Long datasourceId;

    /**
     * 仪表盘图表配置(JSON存储)
     */
    private String snapshot;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 图表状态 0-正常 1-AI生成图表中（不可操作）
     */
    private Integer status;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}