package com.hwq.dataloom.model.dto.datasource_tree;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author HWQ
 * @date 2024/8/19 11:45
 * @description
 */
@Data
public class DeleteDatasourceDirNodeRequest {
    @NotNull(message = "id不得为空")
    private Long id;
}
