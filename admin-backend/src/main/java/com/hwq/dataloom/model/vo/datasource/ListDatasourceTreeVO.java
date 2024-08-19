package com.hwq.dataloom.model.vo.datasource;

import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/8/19 10:01
 * @description 展示文件夹目录树返回类
 */
@Data
public class ListDatasourceTreeVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型 dir（目录）、file（文件）
     */
    private String type;


    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * 子节点列表
     */
    private List<ListDatasourceTreeVO> children;

    /**
     * 父级ID --文件夹
     */
    private Long pid;

    /**
     * 权重
     */
    private Integer wight;
}
