package com.hwq.dataloom.core.workflow.entitys.node.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码节点数据类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CodeNodeData extends BaseNodeData{

    /**
     * 代码 （支持变量插入）
     */
    private String code;

    /**
     * 代码对应的语言
     */
    private String codeLanguage;

    /**
     * 输出结果
     */
    private Output output;

}
