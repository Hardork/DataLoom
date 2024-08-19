package com.hwq.dataloom.model.dto.newdatasource;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ApiDefinition {
    /**
     * 数据表名称
     */
    private String name;

    /**
     * 数据集名称
     */
    private String deTableName;

    /**
     * 数据表描述
     */
    private String desc;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求方式
     */
    private String method = "GET";

    /**
     * 数据字段情况
     */
    private List<TableField> fields;

    /**
     * 数据源数据结构
     */
    private List<Map<String, Object>> jsonFields =new ArrayList<>();

    /**
     * 接口调用请求
     */
    private ApiDefinitionRequest request;

    /**
     * 接口校验状态
     */
    private String status;

    private List<Map<String, Object>> data = new ArrayList<>();

    /**
     * 接口查询超时时间
     */
    private Integer apiQueryTimeout = 10;

    /**
     * 数据预览
     */
    private int previewNum = 100;
    private int serialNumber;

    /**
     * 是否指定JsonPath
     */
    private boolean useJsonPath;

    /**
     * JsonPath
     */
    private String jsonPath;

    private boolean reName = false;
    private String orgName;

    private boolean showApiStructure;
    private Long updateTime;
    private String type = "table";
}
