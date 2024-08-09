package com.hwq.bi.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName chat_history
 */
@TableName(value ="chat_history")
@Data
public class ChatHistory implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 0-用户 1-AI
     */
    @TableField(value = "chatRole")
    private Integer chatRole;

    /**
     * 会话id
     */
    @TableField(value = "chatId")
    private Long chatId;

    /**
     * 助手id
     */
    @TableField(value = "modelId")
    private Long modelId;

    /**
     * 回应内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 特定信息
     */
    @TableField(value = "execMessage")
    private String execMessage;

    /**
     * 消息状态  0-正常 1-异常
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 
     */
    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}