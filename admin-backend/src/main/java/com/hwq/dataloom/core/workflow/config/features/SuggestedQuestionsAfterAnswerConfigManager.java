package com.hwq.dataloom.core.workflow.config.features;

import java.util.Map;

/**
 *
 */
public class SuggestedQuestionsAfterAnswerConfigManager {

    /**
     * 模拟Python中convert方法的功能，将配置信息转换为对应的布尔值，表示是否启用建议问题（在回答后）功能
     *
     * @param config 配置信息的Map结构，对应Python中的字典类型参数
     * @return 布尔值，代表是否启用建议问题（在回答后）功能
     */
    public static boolean convert(Map<String, Object> config) {
        boolean suggestedQuestionsAfterAnswer = false;
        Map<String, Object> suggestedQuestionsAfterAnswerDict = (Map<String, Object>) config.get("suggested_questions_after_answer");
        if (suggestedQuestionsAfterAnswerDict!= null && (boolean) suggestedQuestionsAfterAnswerDict.get("enabled")) {
            suggestedQuestionsAfterAnswer = true;
        }
        return suggestedQuestionsAfterAnswer;
    }
}