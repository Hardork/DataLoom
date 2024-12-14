package com.hwq.dataloom.core.workflow.node.end;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: end流参数
 * @DateTime: 2024/12/14 17:32
 **/
@Data
public class EndStreamParam {
    /**
     * end流依赖对象集合
     */
    private Map<String, List<String>> endDependencies;

    /**
     * end流参数选择器映射
     */
    private Map<String, List<List<String>>> endStreamVariableSelectorMapping;
}
