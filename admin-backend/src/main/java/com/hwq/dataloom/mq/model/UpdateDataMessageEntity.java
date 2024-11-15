package com.hwq.dataloom.mq.model;

import lombok.Data;

import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/15
 * @Description:
 **/
@Data
public class UpdateDataMessageEntity {

    /**
     * 修改者
     */
    private String updateUserName;
    /**
     * 修改内容
     */
    private String updateContent;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 数据集id
     */
    private Long dataId;
    /**
     * 发送方式
     */
    private String type;
}
