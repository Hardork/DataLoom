package com.hwq.dataloom.product.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
@Data
public class ProductOrder {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long pointId;
    private BigDecimal payAmount;
    private String state;
    private Date orderTime;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDelete;
}
