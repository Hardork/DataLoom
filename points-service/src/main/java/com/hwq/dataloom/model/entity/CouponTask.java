package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 优惠券模板发送任务表
 * @TableName coupon_task
 */
@TableName(value ="coupon_task")
@Data
public class CouponTask implements Serializable {
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
     * 优惠券批次任务名称
     */
    private String taskName;

    /**
     * 发放优惠券数量
     */
    private Integer sendNum;

    /**
     * 发放用户列表文件地址
     */
    private String userListFilePath;

    /**
     * 通知方式，可组合使用 0：站内信 1：弹框推送 2：邮箱 3：短信
     */
    private String notifyType;

    /**
     * 优惠券模板ID
     */
    private Long couponTemplateId;

    /**
     * 发送类型 0：立即发送 1：定时发送
     */
    private Integer sendType;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 状态 0：待执行 1：执行中 2：执行失败 3：执行成功 4：取消
     */
    private Integer status;

    /**
     * 完成时间
     */
    private Date completionTime;

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