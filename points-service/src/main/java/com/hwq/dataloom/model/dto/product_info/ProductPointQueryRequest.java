package com.hwq.dataloom.model.dto.product_info;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.hwq.dataloom.framework.request.PageRequest;
import lombok.Data;

import java.util.Date;

/**
 * @Author:HWQ
 * @DateTime:2023/10/10 9:29
 * @Description:
 **/
@Data
public class ProductPointQueryRequest extends PageRequest {
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
}
