package com.hwq.dataloom.model.vo;

import lombok.Data;

/**
 * chart 的返回结果
 */
@Data
public class ChartResponse {

    private String genChart;

    private String genResult;

    private Long chartId;
}
