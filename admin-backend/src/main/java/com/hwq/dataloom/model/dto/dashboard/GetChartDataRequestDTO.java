package com.hwq.dataloom.model.dto.dashboard;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author HWQ
 * @date 2024/9/15 20:39
 * @description 获取数据配置信息
 */
@Data
public class GetChartDataRequestDTO {
    @NotNull(message = "数据配置不得为空")
    private String dataOption;
}
