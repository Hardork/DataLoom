package com.hwq.dataloom.account.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
@Data
public class MemberAccount implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Date vipExpireTime;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDelete;
}
