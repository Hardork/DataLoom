package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 优惠券发放任务失败详情表
 * @TableName coupon_task_fail_record
 */
@TableName(value ="coupon_task_fail_record")
@Data
public class CouponTaskFailRecord implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 批次ID
     */
    private Long batchId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 优惠券模板ID
     */
    private Long couponTemplateId;

    /**
     * 失败内容
     */
    private String failedContent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 操作人 -- 管理源id
     */
    private Long operatorId;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}