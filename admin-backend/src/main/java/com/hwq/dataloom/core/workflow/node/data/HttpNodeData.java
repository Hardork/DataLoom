package com.hwq.dataloom.core.workflow.node.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Http请求节点数据类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HttpNodeData extends BaseNodeData {
    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求体
     */
    private String body;

    /**
     * 请求头
     */
    private String headers;

    /**
     * 请求Cookie
     */
    private String cookies;

    /**
     * 请求体类型
     */
    private String contentType;
}
