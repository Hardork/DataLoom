package com.hwq.dataloom.core.workflow.config.features;

import java.util.List;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: 从配置信息中提取开场陈述和建议问题列
 * @DateTime: 2024/11/26 16:40
 **/
public class OpeningStatementConfigManager {

    /**
     * 从配置信息中提取开场陈述和建议问题列表
     *
     * @param config 配置信息的Map结构
     * @return 包含开场陈述字符串和建议问题列表的对象数组
     */
    public static Object[] convert(Map<String, Object> config) {
        String openingStatement = (String) config.get("opening_statement");

        List<String> suggestedQuestionsList = (List<String>) config.get("suggested_questions");

        return new Object[]{openingStatement, suggestedQuestionsList};
    }
}