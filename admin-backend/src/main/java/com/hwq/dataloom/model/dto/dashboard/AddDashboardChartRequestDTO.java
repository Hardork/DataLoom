package com.hwq.dataloom.model.dto.dashboard;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author HWQ
 * @date 2024/9/12 18:43
 * @description 添加仪表盘图表请求类
 */
@Data
public class AddDashboardChartRequestDTO {
    /**
     * 对应仪表盘id
     */
    @NotNull
    private Long dashboardId;

    /**
     * 图表名称
     */
    @NotEmpty(message = "chartName不得为空")
    private String chartName;

    /**
     * 图表配置
     */
    private String chartOption;

    /**
     * 数据配置
     */
    private String dataOption;

    /**
     * 图表配置对应的自定义sql
     */
    private String customSql;
}
