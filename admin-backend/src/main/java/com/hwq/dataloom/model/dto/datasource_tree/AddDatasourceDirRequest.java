package com.hwq.dataloom.model.dto.datasource_tree;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;


/**
 * @author HWQ
 * @date 2024/8/18 23:50
 * @description 添加数据源请求类
 */
@Data
public class AddDatasourceDirRequest {

    /**
     * 名称
     */
    @NotBlank(message = "名称不得为空")
    private String name;

    /**
     * 类型 dir（目录）、file（文件）
     */
    @NotBlank(message = "类型不得为空")
    private String type;

    /**
     * 父级ID --文件夹
     */
    @NotNull(message = "pid不得为空")
    private Long pid;

    /**
     * 权重
     */
    private Integer wight;
}
