package com.hwq.dataloom.core.workflow.config.features;

import java.util.Map;

/**
 * @Author: HWQ
 * @Description: 将配置信息转换为对应的布尔值，用于表示是否启用“更多相似内容”相关功能
 * @DateTime: 2024/11/26 16:40
 **/
public class MoreLikeThisConfigManager {
    /**
     * 将配置信息转换为对应的布尔值，用于表示是否启用“更多相似内容”相关功能
     *
     * @param config 配置信息的Map结构
     * @return 布尔值，代表是否启用“更多相似内容”功能
     */
    public static boolean convert(Map<String, Object> config) {
        boolean moreLikeThis = false;
        Map<String, Object> moreLikeThisDict = (Map<String, Object>) config.get("more_like_this");
        if (moreLikeThisDict!= null && (boolean) moreLikeThisDict.get("enabled")) {
            moreLikeThis = true;
        }
        return moreLikeThis;
    }
}