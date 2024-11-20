package com.hwq.dataloom.model.json.workflow.node.data;

import lombok.Data;

import java.util.Map;

@Data
public class Output {
    /**
     * 是否成功输出
     */
    private String status;

    /**
     * 输出结果
     */
    private Map<String, Object> results;

    /**
     * 描述
     */
    private String message;
}