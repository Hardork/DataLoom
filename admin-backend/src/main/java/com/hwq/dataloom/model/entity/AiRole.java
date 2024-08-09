package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ai_role
 */
@TableName(value ="ai_role")
@Data
public class AiRole implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 助手名称
     */
    @TableField(value = "assistantName")
    private String assistantName;

    /**
     * 创建人Id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 助手类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 历史对话
     */
    @TableField(value = "historyTalk")
    private Integer historyTalk;

    /**
     * 功能描述
     */
    @TableField(value = "functionDes")
    private String functionDes;

    /**
     * 输入模型
     */
    @TableField(value = "inputModel")
    private String inputModel;

    /**
     * 角色设定
     */
    @TableField(value = "roleDesign")
    private String roleDesign;

    /**
     * 目标任务
     */
    @TableField(value = "targetWork")
    private String targetWork;

    /**
     * 需求说明
     */
    @TableField(value = "requirement")
    private String requirement;

    /**
     * 风格设定
     */
    @TableField(value = "style")
    private String style;

    /**
     * 其它示例
     */
    @TableField(value = "otherRequire")
    private String otherRequire;

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