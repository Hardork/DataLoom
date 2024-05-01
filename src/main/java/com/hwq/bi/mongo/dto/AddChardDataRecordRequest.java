package com.hwq.bi.mongo.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author HWQ
 * @date 2024/4/27 20:46
 * @description
 */
@Data
public class AddChardDataRecordRequest {
    /**
     * 数据集id
     */
    private Long dataId;

    /**
     * 修改后的数据
     */
    private Map<String, Object> data;
}
