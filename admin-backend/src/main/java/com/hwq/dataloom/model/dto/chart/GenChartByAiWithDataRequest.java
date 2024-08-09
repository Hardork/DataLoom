package com.hwq.dataloom.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 * @author <a href="https://github.com/Hardork">老山羊</a>
 * 
 */
@Data
public class GenChartByAiWithDataRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 数据集Id
     */
    private Long dataId;

    private static final long serialVersionUID = 1L;
}
