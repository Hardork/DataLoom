package com.hwq.bi.mongo.dto;

import lombok.Data;

/**
 * @author HWQ
 * @date 2024/4/27 17:06
 * @description
 */
@Data
public class DeleteChartDataRecordRequest {
    /**
     * 数据集id
     */
    private Long dataId;
    /**
     * 数据集对应记录id
     */
    private String id;
}
