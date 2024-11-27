package com.hwq.dataloom.core.workflow.config.features;

import java.util.*;

/**
 * 是否展示检索资源配置管理类
 */
public class RetrievalResourceConfigManager {

    /**
     * 将配置信息转换为对应的布尔值表示是否展示检索资源
     *
     * @param config 配置信息的Map结构，对应Python中的字典
     * @return 布尔值，代表是否展示检索资源
     */
    public static boolean convert(Map<String, Object> config) {
        boolean showRetrieveSource = false;
        Map<String, Object> retrieverResourceDict = (Map<String, Object>) config.get("retriever_resource");
        if (retrieverResourceDict!= null && (boolean) retrieverResourceDict.get("enabled")) {
            showRetrieveSource = true;
        }
        return showRetrieveSource;
    }

}