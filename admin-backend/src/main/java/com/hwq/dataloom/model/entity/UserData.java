package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user_data
 */
@TableName(value ="user_data")
@Data
public class UserData implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建数据用户
     */
    private Long userId;

    /**
     * 数据集名称
     */
    private String dataName;

    /**
     * 数据集描述
     */
    private String description;

    /**
     * 上传类型
     */
    private Integer uploadType;

    /**
     * 字段类型
     */
    private String fieldTypeInfo;

    /**
     * 所有记录
     */
    private Integer totalRecord;

    /**
     * 读密钥
     */
    private String readSecretKey;

    /**
     * 写密钥
     */
    private String writeSecretKey;

    /**
     * 是否需要审批
     */
    private Boolean approvalConfirm;

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