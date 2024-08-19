package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 数据源目录树
 * @TableName datasource_dir_tree
 */
@TableName(value ="datasource_dir_tree")
@Data
public class DatasourceDirTree implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型 dir（目录）、file（文件）
     */
    private String type;


    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * 父级ID --文件夹
     */
    private Long pid;

    /**
     * 权重
     */
    private Integer wight;

    /**
     * 创建的用户ID
     */
    private Long userId;

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
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}