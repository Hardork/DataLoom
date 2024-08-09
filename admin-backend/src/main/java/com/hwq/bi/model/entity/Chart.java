package com.hwq.bi.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 图表信息表
 * @TableName chart
 */
@TableName(value ="chart")
@Data
public class Chart implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分析目标
     */
    @TableField(value = "goal")
    private String goal;

    /**
     * 图表名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 图表数据
     */
    @TableField(value = "chartData")
    private String chartData;

    /**
     * 数据集id
     */
    @TableField(value = "userDataId")
    private Long userDataId;

    /**
     * 图表类型
     */
    @TableField(value = "chartType")
    private String chartType;

    /**
     * 生成的图表数据
     */
    @TableField(value = "genChart")
    private String genChart;

    /**
     * 生成的分析结论
     */
    @TableField(value = "genResult")
    private String genResult;

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
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Chart other = (Chart) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGoal() == null ? other.getGoal() == null : this.getGoal().equals(other.getGoal()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getChartData() == null ? other.getChartData() == null : this.getChartData().equals(other.getChartData()))
            && (this.getChartType() == null ? other.getChartType() == null : this.getChartType().equals(other.getChartType()))
            && (this.getGenChart() == null ? other.getGenChart() == null : this.getGenChart().equals(other.getGenChart()))
            && (this.getGenResult() == null ? other.getGenResult() == null : this.getGenResult().equals(other.getGenResult()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getExecMessage() == null ? other.getExecMessage() == null : this.getExecMessage().equals(other.getExecMessage()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGoal() == null) ? 0 : getGoal().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getChartData() == null) ? 0 : getChartData().hashCode());
        result = prime * result + ((getChartType() == null) ? 0 : getChartType().hashCode());
        result = prime * result + ((getGenChart() == null) ? 0 : getGenChart().hashCode());
        result = prime * result + ((getGenResult() == null) ? 0 : getGenResult().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getExecMessage() == null) ? 0 : getExecMessage().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", goal=").append(goal);
        sb.append(", name=").append(name);
        sb.append(", chartData=").append(chartData);
        sb.append(", chartType=").append(chartType);
        sb.append(", genChart=").append(genChart);
        sb.append(", genResult=").append(genResult);
        sb.append(", status=").append(status);
        sb.append(", execMessage=").append(execMessage);
        sb.append(", userId=").append(userId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}