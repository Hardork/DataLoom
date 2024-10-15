package com.hwq.dataloom.framework.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description:
 **/
@Data
public class UserCreditAccount {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal availableAmount;
    private String accountStatus;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;

}
