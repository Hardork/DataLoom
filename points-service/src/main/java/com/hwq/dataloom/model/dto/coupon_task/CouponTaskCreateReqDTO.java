package com.hwq.dataloom.model.dto.coupon_task;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 创建优惠券发放任务请求类
 */
@Data
public class CouponTaskCreateReqDTO {

    /**
     * 优惠券批次任务名称
     */
    @Schema(description = "优惠券批次任务名称",
            example = "发送百万优惠券推送任务",
            required = true)
    private String taskName;


    /**
     * 文件地址
     */
    @Schema(description = "优惠券批次任务名称",
            // step1 调用 ExcelGenerateTests 里的单元测试用例，生成 Excel 测试示例文件
            // step2 复制项目目录下 tmp/oneCoupon任务推送Excel 的绝对路径，Windows 和 Mac 下有些许区别，Windows 下应该是 D:\Users\xxx\xxx
            example = "/Users/machen/workspace/nageoffer/onecoupon/tmp/oneCoupon任务推送Excel.xlsx",
            required = true)
    private String fileAddress;

    /**
     * 通知方式，可组合使用 0：站内信 1：弹框推送 2：邮箱 3：短信
     */
    @Schema(description = "通知方式",
            example = "0,3",
            required = true)
    private String notifyType;

    /**
     * 优惠券模板id
     */
    @Schema(description = "优惠券模板id",
            example = "1810966706881941507",
            required = true)
    private Long couponTemplateId;

    /**
     * 发送类型 0：立即发送 1：定时发送
     */
    @Schema(description = "发送类型",
            example = "0",
            required = true)
    private Integer sendType;

    /**
     * 发送时间
     */
    @Schema(description = "发送时间", example = "2024-08-20 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;
}