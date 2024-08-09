package com.hwq.bi.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 失败分析表
 * @TableName failed_chart
 */
@TableName(value ="failed_chart")
@Data
public class FailedChart implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 图表id
     */
    @TableField(value = "chartId")
    private Long chartId;

    /**
     * wait,running,succeed,failed
     */
    @TableField(value = "status")
    private String status;

    /**
     * 执行信息
     */
    @TableField(value = "execMessage")
    private String execMessage;

    /**
     * 创建用户 id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 定时重试次数
     */
    @TableField(value = "retryNum")
    private Integer retryNum;

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
     * 是否删除
     */
    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}