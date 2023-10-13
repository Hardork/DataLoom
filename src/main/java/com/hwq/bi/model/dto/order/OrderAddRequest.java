package com.hwq.bi.model.dto.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @Author:HWQ
 * @DateTime:2023/10/11 22:41
 * @Description:
 **/
@Data
public class OrderAddRequest {

    /**
     * 商品id
     */
    @TableField(value = "productId")
    private Long productId;


    /**
     * 产品类型 0-积分服务 1-会员服务
     */
    @TableField(value = "productType")
    private Integer productType;

}
