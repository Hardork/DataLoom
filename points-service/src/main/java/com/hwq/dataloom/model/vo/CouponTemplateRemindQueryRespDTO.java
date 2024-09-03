package com.hwq.dataloom.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CouponTemplateRemindQueryRespDTO {


    /**
     * id
     */
    private Long id;


    /**
     * 优惠券名称
     */
    private String name;


    /**
     * 优惠对象 0：商品专属 1：全店通用
     */
    private Integer target;


    /**
     * 优惠类型 0：立减券 1：满减券 2：折扣券
     */
    private Integer type;

    /**
     * 有效期开始时间
     */
    private Date validStartTime;

    /**
     * 有效期结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validEndTime;

    /**
     * 领取规则
     */
    private String claimRule;

    /**
     * 消耗规则
     */
    private String usageRule;


    /**
     * 提醒的时间，和提醒类型按照顺序一一对应
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private List<Date> remindTime;

    /**
     * 提醒类型，和提醒时间按照顺序一一对应
     */
    private List<String> remindType;
}
