package com.hwq.dataloom.manager.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/15
 * @Description:
 **/
@Data
@Builder
public class UpdateDataBuildMessage {
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
}
