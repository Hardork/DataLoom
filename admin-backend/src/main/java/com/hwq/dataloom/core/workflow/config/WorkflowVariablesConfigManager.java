package com.hwq.dataloom.core.workflow.config;

import com.hwq.dataloom.core.workflow.entitys.VariableEntity;
import com.hwq.dataloom.model.entity.Workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WorkflowVariablesConfigManager {

    /**
     * 将工作流的起始变量转换为VariableEntity列表
     *
     * @param workflow 工作流实例
     * @return VariableEntity类型的列表
     */
    public static List<VariableEntity> convert(Workflow workflow) {
        List<VariableEntity> variables = new ArrayList<>();

        // 查找起始节点（这里假设Workflow类有对应的user_input_form方法来获取相关输入表单数据，需根据实际情况实现）
        List<Map<String, Object>> userInputForm = workflow.userInputForm();

        // TODO: 遍历变量并进行转换添加到列表中（这里假设VariableEntity类有合适的构造函数或者静态方法来从Object类型数据进行初始化，需根据实际情况调整）


        return variables;
    }
}