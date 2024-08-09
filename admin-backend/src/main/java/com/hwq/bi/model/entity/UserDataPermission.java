package com.hwq.bi.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user_data_permission
 */
@TableName(value ="user_data_permission")
@Data
public class UserDataPermission implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 对应数据集id
     */
    private Long dataId;

    /**
     * 对应用户id
     */
    private Long userId;

    /**
     * 权限
     */
    private Integer permission;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}