package com.hwq.dataloom.model.dto.newdatasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据集分组表
 * @TableName core_dataset_group
 */
@Data
public class CoreDatasetGroupDTO implements Serializable {
    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 父级ID
     */
    private Long pid;

    /**
     * 当前分组处于第几级
     */
    private Integer level;

    /**
     * node类型：folder or dataset
     */
    private String nodeType;

    /**
     * sql,union
     */
    private String type;

    /**
     * 连接模式：0-直连，1-同步(包括excel、api等数据存在de中的表)
     */
    private Integer mode;

    /**
     * 关联关系树
     */
    private String info;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 关联sql
     */
    private String unionSql;

    private List<UnionDTO> union;// 关联数据集

//    private List<DeSortField> sortFields;// 自定义排序（如仪表板查询组件）

    private Map<String, List> data;

    private List<CoreDatasetTableField> allFields;

    private String sql;

    private Long total;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}