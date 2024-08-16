package com.hwq.dataloom.model.dto.product_info;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @Author:HWQ
 * @DateTime:2023/10/10 8:48
 * @Description:
 **/
@Data
public class ProductPointAddRequest {
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
}
