package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 产品信息
 * @TableName product_point
 */
@TableName(value ="product_point")
@Data
public class ProductPoint implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 产品名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 产品描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 创建人
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 金额(分)
     */
    @TableField(value = "total")
    private Long total;

    /**
     * 原价(分)
     */
    @TableField(value = "originalTotal")
    private Long originalTotal;

    /**
     * 增加积分个数
     */
    @TableField(value = "addPoints")
    private Long addPoints;

    /**
     * 商品状态（0- 默认下线 1- 上线）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 商品过期时间
     */
    @TableField(value = "expirationTime")
    private Date expirationTime;

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