package com.hwq.dataloom.core.workflow.node.end;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: end流参数
 * @DateTime: 2024/12/14 17:32
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndStreamParam {

    /**
     * end流参数选择器映射
     */
    private Map<String, List<List<String>>> endStreamVariableSelectorMapping;

    /**
     * end流依赖对象集合
     */
    private Map<String, List<String>> endDependencies;


}
