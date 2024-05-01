package com.hwq.bi.mongo.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author HWQ
 * @date 2024/4/27 17:07
 * @description
 */
@Data
public class EditChartDataRecordRequest {
    /**
     * 数据集id
     */
    private Long dataId;
    /**
     * 数据集对应记录id
     */
    private String id;
    /**
     * 修改后的数据
     */
    private Map<String, Object> data;
}
