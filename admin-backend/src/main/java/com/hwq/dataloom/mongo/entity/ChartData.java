package com.hwq.dataloom.mongo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author HWQ
 * @date 2024/4/19 17:03
 * @description
 */
@Data
@Accessors(chain = true)
public class ChartData {

    /**
     * id 可不填
     */
    private String id;

    /**
     * 数据
     */
    private Map<String, Object> data;

}
