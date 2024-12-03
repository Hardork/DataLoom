package com.hwq.dataloom.core.workflow.entitys.variable;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.hwq.dataloom.core.file.FileAttribute;
import com.hwq.dataloom.core.workflow.constants.WorkflowConstant;
import com.hwq.dataloom.core.workflow.entitys.variable.segment.FileSegment;
import com.hwq.dataloom.core.workflow.entitys.variable.segment.Segment;
import com.hwq.dataloom.core.workflow.enums.SystemVariableKey;
import com.hwq.dataloom.core.workflow.factory.VariableBuilder;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流变量池
 */
public class VariablePool {
    // variable_dictionary，使用嵌套的Map结构
    private Map<String, Map<Integer, Segment>> variableDictionary = new HashMap<>();
    // user_inputs，使用Map来存储用户输入相关数据（简化结构，实际按业务完善）
    private Map<String, Object> userInputs = new HashMap<>();
    // system_variables，使用Map存储系统变量（按SystemVariableKey作为键，实际按业务完善）
    private Map<SystemVariableKey, Object> systemVariables = new HashMap<>();
    // environment_variables，使用List存储环境变量（简化为存储Variable实例，实际按业务完善）
    private List<VariableEntity> environmentVariables = new ArrayList<>();
    // conversation_variables，使用List存储会话变量（同样简化，按实际完善）
    private List<VariableEntity> conversationVariables = new ArrayList<>();

    // 构造函数初始化逻辑
    public VariablePool(Map<SystemVariableKey, Object> system_variables, Map<String, Object> user_inputs,
                        Collection<VariableEntity> environment_variables, Collection<VariableEntity> conversationVariables) {
        this.systemVariables = Optional.ofNullable(system_variables).orElse(MapUtil.empty());
        this.userInputs = Optional.ofNullable(user_inputs).orElse(MapUtil.empty());
        this.environmentVariables = new ArrayList<>(Optional.ofNullable(environment_variables).orElse(ListUtil.empty()));
        this.conversationVariables = new ArrayList<>(Optional.of(conversationVariables).orElse(ListUtil.empty()));

        // 模拟Python中添加系统变量到变量池的逻辑
        this.systemVariables.forEach((key, value) -> {
            add(Arrays.asList(WorkflowConstant.SYSTEM_VARIABLE_NODE_ID, key.name()), value);
        });

        // 模拟添加环境变量到变量池的逻辑
        this.environmentVariables.forEach(var -> {
            add(Arrays.asList(WorkflowConstant.ENVIRONMENT_VARIABLE_NODE_ID, var.getLabel()), var);
        });

        // 模拟添加会话变量到变量池的逻辑
        this.conversationVariables.forEach(var -> {
            add(Arrays.asList(WorkflowConstant.CONVERSATION_VARIABLE_NODE_ID, var.getLabel()), var);
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

    // get方法逻辑
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
            Segment resultSegment = VariableBuilder.buildSegment(attrValue);
            return resultSegment;
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