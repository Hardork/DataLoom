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
    /** 优惠券id */
    private Long pointId;
    /** 优惠券金额 */
    private BigDecimal pointAmount;
    /** 原始金额 */
    private BigDecimal originalAmount;
    /** 购买金额 */
    private BigDecimal payAmount;
    /** 订单状态 */
    private String state;
    /** 下单时间 */
    private Date orderTime;
    /** 流水单号 */
    private String outBusinessNo;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDelete;
}
