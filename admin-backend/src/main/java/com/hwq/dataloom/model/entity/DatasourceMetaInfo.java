package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName datasource_meta_info
 */
@TableName(value ="datasource_meta_info")
@Data
public class DatasourceMetaInfo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源描述
     */
    private String description;

    /**
     * 数据源类型
     */
    private Integer type;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口号
     */
    private String port;

    /**
     * 数据库名称
     */
    private String dataBaseName;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 初始连接数
     */
    private Integer initConNum;

    /**
     * 最大连接数
     */
    private Integer maxConNum;

    /**
     * 连接超时时间
     */
    private Integer timeoutSecond;

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
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}