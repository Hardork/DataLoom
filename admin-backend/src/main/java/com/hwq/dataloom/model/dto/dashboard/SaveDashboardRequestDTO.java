package com.hwq.dataloom.model.dto.dashboard;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author HWQ
 * @date 2024/9/12 18:43
 * @description 保存仪表盘请求类
 */
@Data
public class SaveDashboardRequestDTO {
    /**
     * 仪表盘id
     */
    @NotNull(message = "dashboardId不得为空")
    Long id;

    /**
     * 仪表盘名称
     */
    private String name;

    /**
     * 仪表盘图表配置(JSON存储)
     */
    private String snapshot;
}
