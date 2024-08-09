package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品订单
 * @TableName product_order
 */
@TableName(value ="product_order")
@Data
public class ProductOrder implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField(value = "orderNo")
    private String orderNo;

    /**
     * 二维码地址
     */
    @TableField(value = "codeUrl")
    private String codeUrl;

    /**
     * 创建人
     */
    @TableField(value = "userId")
    private Long userId;

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

    /**
     * 交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)
     */
    @TableField(value = "status")
    private String status;

    /**
     * 支付方式（默认 WX- 微信 ZFB- 支付宝）
     */
    @TableField(value = "payType")
    private String payType;

    /**
     * 商品信息
     */
    @TableField(value = "productInfo")
    private String productInfo;

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

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}