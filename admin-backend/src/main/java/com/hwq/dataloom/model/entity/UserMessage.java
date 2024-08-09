package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user_message
 */
@TableName(value ="user_message")
@Data
public class UserMessage implements Serializable {
    /**
     * 消息id
     */
    @TableId(value = "id")
    private Long id;


    @TableField(value = "userId")
    private Long userId;

    /**
     * 消息标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 内容
     */
    @TableField(value = "description")
    private String description;

    /**
     * 0-普通 1-成功 2-失败
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 消息对应跳转的路由
     */
    @TableField(value = "route")
    private String route;

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

    /**
     * 
     */
    @TableField(value = "isRead")
    private Integer isRead;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}