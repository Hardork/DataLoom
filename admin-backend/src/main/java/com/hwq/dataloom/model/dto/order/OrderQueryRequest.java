package com.hwq.dataloom.model.dto.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hwq.dataloom.framework.request.PageRequest;
import lombok.Data;

import java.util.Date;

/**
 * @Author:HWQ
 * @DateTime:2023/10/11 23:32
 * @Description:
 **/
@Data
public class OrderQueryRequest extends PageRequest {
    /**
     * 订单号
     */
    @TableField(value = "orderNo")
    private String orderNo;


    /**
     * 商品id
     */
    @TableField(value = "productId")
    private Long productId;

    /**
     * 商品名称
     */
    @TableField(value = "orderName")
    private String orderName;

    /**
     * 金额(分)
     */
    @TableField(value = "total")
    private Long total;

    /**
     * 产品类型 0-积分服务 1-会员服务
     */
    @TableField(value = "productType")
    private Integer productType;


    @TableField(value = "status")
    private String status;

    /**
     * 支付方式（默认 WX- 微信 ZFB- 支付宝）
     */
    @TableField(value = "payType")
    private String payType;


    /**
     * 增加积分个数
     */
    @TableField(value = "addPoints")
    private Long addPoints;

    /**
     * 过期时间
     */
    @TableField(value = "expirationTime")
    private Date expirationTime;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;
}
