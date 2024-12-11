package com.hwq.dataloom.core.workflow.utils;

import com.hwq.dataloom.core.workflow.variable.VariableSelector;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: HWQ
 * @Description: 变量模版转换器
 * @DateTime: 2024/12/11 17:48
 **/
public class VariableTemplateParser {
    // 正则表达式模式
    private static final Pattern REGEX = Pattern.compile("\\{\\{(#([a-zA-Z0-9_]{1,50}(\\.[a-zA-Z_][a-zA-Z0-9_]{0,29}){1,10}#))\\}\\}");

    private String template;  // 原始模板字符串
    private List<String> variableKeys;  // 提取的变量键列表

    /**
     * 构造函数，初始化模板字符串并提取变量键
     *
     * @param template 模板字符串
     */
    public VariableTemplateParser(String template) {
        this.template = template;
        this.variableKeys = extract();
    }

    /**
     * 从模板字符串中提取所有的模板变量键
     *
     * @return 模板变量键的列表
     */
    public List<String> extract() {
        // 查找所有匹配的结果
        Matcher matcher = REGEX.matcher(template);
        List<String> firstGroupMatches = new ArrayList<>();
        while (matcher.find()) {
            firstGroupMatches.add(matcher.group(1));
        }
        // 去重
        return new ArrayList<>(new HashSet<>(firstGroupMatches));
    }

    /**
     * 从模板变量键中提取变量选择器
     *
     * @return 变量选择器的列表
     */
    public List<VariableSelector> extractVariableSelectors() {
        List<VariableSelector> variableSelectors = new ArrayList<>();
        for (String variableKey : variableKeys) {
            String removeHash = variableKey.replace("#", "");
            String[] splitResult = removeHash.split("\\.");
            if (splitResult.length < 2) {
                continue;
            }
            variableSelectors.add(new VariableSelector(variableKey, Arrays.asList(splitResult)));
        }
        return variableSelectors;
    }

    /**
     * 根据输入值格式化模板字符串
     *
     * @param inputs 包含模板变量值的映射
     * @return 格式化后的字符串
     */
    public String format(Map<String, Object> inputs) {
        StringBuffer prompt = new StringBuffer();
        Matcher matcher = REGEX.matcher(template);
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = inputs.get(key);
            if (value == null) {
                value = matcher.group(0);
            }
            if (value == null) {
                value = "";
            }
            if (value instanceof List || value instanceof Map || value instanceof Boolean || value instanceof Integer || value instanceof Float) {
                value = String.valueOf(value);
            }
            matcher.appendReplacement(prompt, removeTemplateVariables(String.valueOf(value)));
        }
        matcher.appendTail(prompt);
        return prompt.toString().replaceAll("<\\|.*?\\|>", "");
    }

    /**
     * 从给定的文本中移除模板变量
     *
     * @param text 输入的文本
     * @return 移除模板变量后的文本
     */
    public static String removeTemplateVariables(String text) {
        return text.replaceAll(REGEX.pattern(), "{${1}}");
    }
}


