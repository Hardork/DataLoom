package com.hwq.dataloom.core.workflow.prompt;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提示模板解析器类
 */
@Data
public class PromptTemplateParser {

    // 不包含特殊变量模板的正则表达式
    private static final Pattern REGEX = Pattern.compile("\\{\\{([a-zA-Z_][a-zA-Z0-9_]{0,29}|#histories#|#query#|#context#)\\}\\}");
    // 包含特殊变量模板的正则表达式
    private static final Pattern WITH_VARIABLE_TMPL_REGEX = Pattern.compile("\\{\\{([a-zA-Z_][a-zA-Z0-9_]{0,29}|#[a-zA-Z0-9_]{1,50}\\.([a-zA-Z0-9_\\.]{1,100})#|#histories#|#query#|#context#)\\}\\}");

    private final String template;  // 模板字符串
    private final boolean withVariableTmpl;  // 是否包含特殊变量模板
    private final Pattern regex;  // 用于匹配的正则表达式
    private List<String> variableKeys;  // 提取的变量键列表

    /**
     * 构造函数
     *
     * @param template           模板字符串
     * @param withVariableTmpl  是否包含特殊变量模板，默认为 false
     */
    public PromptTemplateParser(String template, boolean withVariableTmpl) {
        this.template = template;
        this.withVariableTmpl = withVariableTmpl;
        this.regex = withVariableTmpl? WITH_VARIABLE_TMPL_REGEX : REGEX;
        this.variableKeys = extract();
    }

    /**
     * 从模板字符串中提取符合规则的变量键
     *
     * @return 变量键列表
     */
    public List<String> extract() {
        // 执行正则匹配并获取结果
        Matcher matcher = regex.matcher(template);
        List<String> foundKeys = new ArrayList<>();
        while (matcher.find()) {
            foundKeys.add(matcher.group(1));
        }
        return foundKeys;
    }

    /**
     * 根据输入值格式化模板字符串
     *
     * @param inputs               包含变量值的映射
     * @param removeTemplateVariables 是否移除模板变量
     * @return 格式化后的字符串
     */
    public String format(Map<String, Object> inputs, boolean removeTemplateVariables) {
        StringBuffer prompt = new StringBuffer();
        Matcher matcher = regex.matcher(template);
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = inputs.get(key);
            if (value == null) {
                value = matcher.group(0);
            }

            // 根据条件处理替换值
            if (removeTemplateVariables && value instanceof String) {
                matcher.appendReplacement(prompt, removeTemplateVariables((String) value, withVariableTmpl));
            } else {
                matcher.appendReplacement(prompt, String.valueOf(value));
            }
        }
        matcher.appendTail(prompt);

        // 移除特定标记
        return prompt.toString().replaceAll("<\\|.*?\\|>", "");
    }

    /**
     * 从给定文本中移除模板变量
     *
     * @param text  输入文本
     * @param withVariableTmpl 是否包含特殊变量模板
     * @return 移除模板变量后的文本
     */
    public static String removeTemplateVariables(String text, boolean withVariableTmpl) {
        return text.replaceAll((withVariableTmpl? WITH_VARIABLE_TMPL_REGEX : REGEX).pattern(), "{$1}");
    }
}