package com.hwq.dataloom.model.dto.dashboard;

import lombok.Data;

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
    private Long pid;

    /**
     * 仪表盘名称
     */
    private String name;

    /**
     * 图表配置快照
     */
    private String chartSnapshot;
}
