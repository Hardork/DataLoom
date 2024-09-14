package com.hwq.dataloom.model.dto.dashboard;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author HWQ
 * @date 2024/9/8 12:40
 * @description 添加仪表盘请求类
 */
@Data
public class AddDashboardRequestDTO {
    /**
     * 父节点id
     */
    @NotNull(message = "pid不得为空")
    private Long pid;

    /**
     * 仪表盘名称
     */
    @NotNull(message = "名称不得为空")
    @Length(max = 255, message = "名称长度不得大于255字符")
    private String name;

    /**
     * 数据源ID
     */
    @NotNull(message = "datasourceId不得为空")
    private Long datasourceId;

    /**
     * 仪表盘图表配置(JSON存储)
     */
    private String snapshot;

}
