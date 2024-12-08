package com.hwq.dataloom.core.workflow.config;

import com.hwq.dataloom.core.workflow.variable.VariableEntity;
import com.hwq.dataloom.model.entity.Workflow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: HWQ
 * @Description: 工作流起始变量管理类
 * @DateTime: 2024/11/26 16:40
 **/
public class WorkflowVariablesConfigManager {

    /**
     * 将工作流的起始变量转换为VariableEntity列表
     *
     * @param workflow 工作流实例
     * @return VariableEntity类型的列表
     */
    public static List<VariableEntity> convert(Workflow workflow) {

        // 查找起始节点的输入表单
        List<Map<String, Object>> userInputForm = workflow.userInputForm();

        // 遍历变量并进行转换添加到列表中
        return userInputForm.stream()
                .map(VariableEntity::convertMap2Entity)
                .collect(Collectors.toList());
    }
}