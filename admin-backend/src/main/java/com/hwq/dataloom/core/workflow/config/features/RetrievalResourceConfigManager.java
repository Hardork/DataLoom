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

    /**
     * 校验并设置默认值
     *
     * @param config 应用模型配置参数的Map结构
     * @return 包含处理后的配置信息Map和需要验证的字段列表的元组（在Java中用数组模拟）
     * @throws IllegalArgumentException 如果配置不符合要求则抛出异常
     */
//    public static Map<String, Object>[] validateAndSetDefaults(Map<String, Object> config) {
//        if (config.get("retriever_resource") == null) {
//            config.put("retriever_resource", new HashMap<String, Object>() {{
//                put("enabled", false);
//            }});
//        }
//
//        if (!(config.get("retriever_resource") instanceof Map)) {
//            throw new IllegalArgumentException("retriever_resource must be of dict type");
//        }
//
//        Map<String, Object> retrieverResource = (Map<String, Object>) config.get("retriever_resource");
//        if (!retrieverResource.containsKey("enabled") ||!(retrieverResource.get("enabled") instanceof Boolean)) {
//            retrieverResource.put("enabled", false);
//        }
//
//        if (!(retrieverResource.get("enabled") instanceof Boolean)) {
//            throw new IllegalArgumentException("enabled in retriever_resource must be of boolean type");
//        }
//        List<String> fieldsToValidate = new ArrayList<>();
//        fieldsToValidate.add("retriever_resource");
//        // 在Java中没有直接对应Python元组的结构，这里用数组来模拟返回包含两个元素的结果，第一个是处理后的配置Map，第二个是字段列表
//        return new Map[]{config, fieldsToValidate};
//    }
}