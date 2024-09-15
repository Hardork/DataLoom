package com.hwq.dataloom.model.json;

import lombok.Data;

/**
 * @author HWQ
 * @date 2024/9/15 22:10
 * @description 数值列配置
 */
@Data
public class Series {
    private String fieldName;
    private String rollup;

    @Override
    public String toString() {
        return "Series{" +
                "fieldName='" + fieldName + '\'' +
                ", rollup='" + rollup + '\'' +
                '}';
    }
}
