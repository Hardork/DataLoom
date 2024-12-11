package com.hwq.dataloom.core.workflow.variable;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.hwq.dataloom.core.file.FileAttribute;
import com.hwq.dataloom.core.workflow.constants.WorkflowConstant;
import com.hwq.dataloom.core.workflow.variable.segment.FileSegment;
import com.hwq.dataloom.core.workflow.variable.segment.Segment;
import com.hwq.dataloom.core.workflow.enums.SystemVariableKey;
import com.hwq.dataloom.core.workflow.factory.VariableBuilder;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流变量池
 */
@Data
public class VariablePool {
    // variable_dictionary，使用嵌套的Map结构
    private Map<String, Map<Integer, Segment>> variableDictionary;
    // user_inputs，使用Map来存储用户输入相关数据（简化结构，实际按业务完善）
    private Map<String, Object> userInputs;
    // system_variables，使用Map存储系统变量（按SystemVariableKey作为键，实际按业务完善）
    private Map<SystemVariableKey, Object> systemVariables;
    // environment_variables，使用List存储环境变量（简化为存储Variable实例，实际按业务完善）
    private List<Variable> environmentVariables;
    // conversation_variables，使用List存储会话变量（同样简化，按实际完善）
    private List<Variable> conversationVariables;

    public VariablePool(Map<SystemVariableKey, Object> systemVariables, Map<String, Object> userInputs, List<Variable> environmentVariables) {
        this.systemVariables = systemVariables;
        this.userInputs = userInputs;
        this.environmentVariables = environmentVariables;
    }

    // 构造函数初始化逻辑
    public VariablePool(Map<SystemVariableKey, Object> system_variables, Map<String, Object> user_inputs,
                        Collection<Variable> environment_variables, Collection<Variable> conversationVariables) {
        this.systemVariables = Optional.ofNullable(system_variables).orElse(MapUtil.empty());
        this.userInputs = Optional.ofNullable(user_inputs).orElse(MapUtil.empty());
        this.environmentVariables = new ArrayList<>(Optional.ofNullable(environment_variables).orElse(ListUtil.empty()));
        this.conversationVariables = new ArrayList<>(Optional.of(conversationVariables).orElse(ListUtil.empty()));

        // 添加系统变量到变量池
        this.systemVariables.forEach((key, value) -> {
            add(Arrays.asList(WorkflowConstant.SYSTEM_VARIABLE_NODE_ID, key.name()), value);
        });

        // 添加环境变量到变量池
        this.environmentVariables.forEach(var -> {
            add(Arrays.asList(WorkflowConstant.ENVIRONMENT_VARIABLE_NODE_ID, var.getName()), var);
        });

        // 添加会话变量到变量池
        this.conversationVariables.forEach(var -> {
            add(Arrays.asList(WorkflowConstant.CONVERSATION_VARIABLE_NODE_ID, var.getName()), var);
        });
    }

    // add方法逻辑
    public void add(List<String> selector, Object value)  {
        if (selector.size() < 2) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Invalid selector");
        }
        Segment v = value instanceof Segment? (Segment) value : VariableBuilder.buildVariableFromMapping((Map<String, Object>) value);
        int hash_key = Objects.hash(selector.subList(1, selector.size()).toArray());
        variableDictionary.computeIfAbsent(selector.get(0), k -> new HashMap<>()).put(hash_key, v);
    }


    /**
     * 获取变量片段
     * @param selector 选择器
     * @return 变量片段
     */
    public Segment get(List<String> selector) {
        if (selector.size() < 2) {
            return null;
        }
        int hash_key = Objects.hash(selector.subList(1, selector.size()).toArray());
        Map<Integer, Segment> innerMap = variableDictionary.get(selector.get(0));
        if (innerMap == null) {
            return null;
        }
        Segment value = innerMap.get(hash_key);

        if (value == null) {
            List<String> newSelector = selector.subList(0, selector.size() - 1);
            String attr = selector.get(selector.size() - 1);
            if (!Arrays.stream(FileAttribute.values()).map(Enum::name).collect(Collectors.toList())
                  .contains(attr)) {
                return null;
            }
            Segment fileSegment = get(newSelector);
            if (!(fileSegment instanceof FileSegment)) {
                return null;
            }
            FileAttribute fileAttr = FileAttribute.valueOf(attr);
            // 这里假设存在获取文件属性值的方法（实际按业务完善逻辑，目前简单返回null示例）
            Object attrValue = null;
            return VariableBuilder.buildSegment(attrValue);
        }

        return value;
    }

    // remove方法逻辑
    public void remove(List<String> selector) {
        if (selector.isEmpty()) {
            return;
        }
        if (selector.size() == 1) {
            variableDictionary.put(selector.get(0), new HashMap<>());
            return;
        }
        int hash_key = Objects.hash(selector.subList(1, selector.size()).toArray());
        Map<Integer, Segment> innerMap = variableDictionary.get(selector.get(0));
        if (innerMap!= null) {
            innerMap.remove(hash_key);
        }
    }

    // TODO: 完善convert_template方法逻辑
//
//    // convert_template方法逻辑
//    public SegmentGroup convertTemplate(String template) {
//        List<String> parts = Arrays.asList(template.split("(\\{\\{#([a-zA-Z0-9_]{1,50}(?:\\.[a-zA-Z_][a-zA-Z0-9_]{0,29}){1,10})#}})"));
//        parts = parts.stream().filter(Objects::nonNull).collect(Collectors.toList());
//
//        List<Segment> segments = new ArrayList<>();
//        for (String part : parts) {
//            if (part.contains(".")) {
//                List<String> subParts = Arrays.asList(part.split("\\."));
//                Segment variable = get(subParts);
//                if (variable!= null) {
//                    segments.add(variable);
//                } else {
//                    segments.add(variable_factory.build_segment(part));
//                }
//            } else {
//                segments.add(variable_factory.build_segment(part));
//            }
//        }
//
//        return new SegmentGroup(segments);
//    }

    // get_file方法逻辑
    public FileSegment getFile(List<String> selector) {
        Segment segment = get(selector);
        if (segment instanceof FileSegment) {
            return (FileSegment) segment;
        }
        return null;
    }
}