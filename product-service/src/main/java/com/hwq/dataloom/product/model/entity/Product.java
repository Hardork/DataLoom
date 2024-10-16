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
public class Product {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Integer stockCount;
    private Integer stockCountSurplus;
    private BigDecimal productAmount;
    private String productDesc;
    private String availablePointType;
    private String productConfig;
    private Date beginDateTime;
    private Date endDateTime;
    private String type;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDelete;
}
