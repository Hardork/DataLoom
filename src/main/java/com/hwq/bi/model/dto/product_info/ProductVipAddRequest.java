package com.hwq.bi.model.dto.product_info;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @Author:HWQ
 * @DateTime:2023/10/10 8:49
 * @Description:
 **/
@Data
public class ProductVipAddRequest {
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
     * 金额(分) 数据里的100表示100分，即1元
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
     * 增加积分个数
     */
    @TableField(value = "duration")
    private Integer duration;

    /**
     * 产品类型（0-vip 1-sVip）
     */
    @TableField(value = "productType")
    private Integer productType;

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
