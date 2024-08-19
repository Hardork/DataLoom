package com.hwq.dataloom.model.dto.datasource;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * @author HWQ
 * @date 2024/8/19 10:23
 * @description
 */
@Data
public class MoveDatasourceDirNodeRequest {
    /**
     * 主键
     */
    @NotNull(message = "id不得为空")
    private Long id;


    /**
     * 父级ID --文件夹
     */
    @NotNull(message = "newPid不得为空")
    private Long newPid;

    /**
     * 权重
     */
    private Integer wight;

}
