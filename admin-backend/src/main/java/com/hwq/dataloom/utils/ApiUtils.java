package com.hwq.dataloom.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ApiUtils {

    private static String path = "['%s']";

    public static ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 处理接口返回的JSON
     *
     * @param jsonStr
     * @param fields
     * @param rootPath
     */
    public static void handleStr(String jsonStr, List<Map<String, Object>> fields, String rootPath) {
        if (jsonStr.startsWith("[")) {
            // 当JSON为数组时
            TypeReference<List<Object>> listTypeReference = new TypeReference<List<Object>>() {
            };
            List<Object> jsonArray = null;

            try {
                jsonArray = objectMapper.readValue(jsonStr, listTypeReference);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            for (Object o : jsonArray) {
                handleStr(o.toString(), fields, rootPath);
            }
        } else {
            // 当JSON不为数组时
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(jsonStr);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            Iterator<String> fieldNames = jsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                String value = jsonNode.get(fieldName).toString();
                // 处理简单类型的字段
                if (StringUtils.isNotEmpty(value) && !value.startsWith("[") && !value.startsWith("{")) {
                    value = jsonNode.get(fieldName).asText();
                }
                // 处理复杂类型的字段
                if (StringUtils.isNotEmpty(value) && value.startsWith("[")) {
                    // 处理数组类型的字段
                    HashMap<String, Object> map = new HashMap<>();

                    try {
                        JsonNode jsonArray = objectMapper.readTree(value);
                        List<Map<String, Object>> childrenField = new ArrayList<>();
                        for (JsonNode node : jsonArray) {
                            if (StringUtils.isNotEmpty(node.toString()) && !node.toString().startsWith("[") && !node.toString().startsWith("{")) {
                                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不为JSON格式");
                            }
                        }
                        for (JsonNode node : jsonArray) {
                            handleStr(node.toString(), childrenField, rootPath + "." + String.format(path, fieldName) + "[*]");
                        }
                        map.put("children", childrenField);
                        map.put("childrenDataType", "LIST");
                    } catch (JsonProcessingException e) {
                        JsonArray array = new JsonArray();
                        array.add(StringUtils.isNotEmpty(value) ? value : "");
                        map.put("value", array);
                    }
                    map.put("jsonPath", rootPath + "." + String.format(path, fieldName));

                    setProperty(map, fieldName);

                    // 相同字段合并
                    if (!hasItem(fields,map)){
                        fields.add(map);
                    }


                } else if (StringUtils.isNotEmpty(value) && value.startsWith("{")) {
                    // 处理JSON对象字段
                    HashMap<String, Object> map = new HashMap<>();

                    try {
                        JsonNode jsonNode1 = objectMapper.readTree(value);
                        List<Map<String, Object>> children = new ArrayList<>();
                        handleStr(value, children, rootPath + "." + String.format(path, fieldName));
                        map.put("children", children);
                        map.put("childrenDataType", "OBJECT");
                        map.put("jsonPath", rootPath + "." + fieldName);
                    } catch (JsonProcessingException e) {
                        map.put("jsonPath", rootPath + "." + String.format(path, fieldName));
                        JSONArray array = new JSONArray();
                        array.add(StringUtils.isNotEmpty(value) ? value : "");
                        map.put("value", array);
                    }

                    setProperty(map, fieldName);

                    // 相同字段合并
                    if (!hasItem(fields,map)){
                        fields.add(map);
                    }

                } else {
                    // 处理普通类型的字段
                    HashMap<String, Object> map = new HashMap<>();

                    map.put("jsonPath", rootPath + "." + String.format(path, fieldName));
                    JSONArray array = new JSONArray();
                    array.add(StringUtils.isNotEmpty(value) ? value : "");
                    map.put("value", array);

                    setProperty(map, fieldName);

                    // 相同字段合并
                    if (!hasItem(fields,map)){
                        fields.add(map);
                    }

                }
            }
        }
    }


    /**
     * 设置TableField属性
     *
     * @param map
     * @param s
     */
    public static void setProperty(Map<String, Object> map, String s) {
        map.put("originName", s);
        map.put("name", s);
        map.put("type", "STRING");
        map.put("size", 65535);
        map.put("deExtractType", 0);
        map.put("deType", 0);
        map.put("checked", false);
//        if (!apiDefinition.isUseJsonPath()) {
//            for (TableField field : apiDefinition.getFields()) {
//                if (!ObjectUtils.isEmpty(o.get("jsonPath")) && StringUtils.isNotEmpty(field.getJsonPath()) && field.getJsonPath().equals(o.get("jsonPath").toString())) {
//                    o.put("checked", true);
//                    o.put("name", field.getName());
//                    o.put("deExtractType", field.getDeExtractType());
//                }
//            }
//        }
    }

    public static boolean hasItem(List<Map<String, Object>> fields, Map<String, Object> item) {
        boolean hasItem = false;
        for (Map<String, Object> field : fields) {
            if (field.get("jsonPath").equals(item.get("jsonPath"))) {
                hasItem = true;
                mergeField(field, item);
                mergeValue(field, item);
                break;
            }
        }
        return hasItem;
    }

    public static void mergeField(Map<String, Object> field, Map<String, Object> item) {
        TypeReference<List<Map<String, Object>>> listForMapTypeReference = new TypeReference<List<Map<String, Object>>>() {
        };
        if (item.get("children") != null) {
            List<Map<String, Object>> fieldChildren = null;
            List<Map<String, Object>> itemChildren = null;
            try {
                fieldChildren = objectMapper.readValue(JSONUtil.toJsonStr(field.get("children")), listForMapTypeReference);
                itemChildren = objectMapper.readValue(JSONUtil.toJsonStr(item.get("children")), listForMapTypeReference);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (fieldChildren == null) {
                fieldChildren = new ArrayList<>();
            }
            for (Map<String, Object> itemChild : itemChildren) {
                boolean same = false;
                for (Map<String, Object> fieldChild : fieldChildren) {
                    if (itemChild.get("jsonPath").toString().equals(fieldChild.get("jsonPath").toString())) {
                        mergeField(fieldChild, itemChild);
                        same = true;
                    }
                }
                if (!same) {
                    fieldChildren.add(itemChild);
                }
            }

        }
    }

    public static void mergeValue(Map<String, Object> field, Map<String, Object> item) {
        TypeReference<JSONArray> listTypeReference = new TypeReference<JSONArray>() {
        };
        TypeReference<List<Map<String, Object>>> listForMapTypeReference = new TypeReference<List<Map<String, Object>>>() {
        };
        if (ObjectUtils.isNotEmpty(field.get("value")) && ObjectUtils.isNotEmpty(item.get("value"))) {
            try {
                JSONArray array = objectMapper.readValue(JSONUtil.toJsonStr(field.get("value")), listTypeReference);
                array.add(objectMapper.readValue(JSONUtil.toJsonStr(item.get("value")), listTypeReference).get(0));
                field.put("value", array);
                if (ObjectUtils.isNotEmpty(field.get("children")) && ObjectUtils.isNotEmpty(item.get("children"))) {
                    List<Map<String, Object>> fieldChildren = objectMapper.readValue(JSONUtil.toJsonStr(field.get("children")), listForMapTypeReference);
                    List<Map<String, Object>> itemChildren = objectMapper.readValue(JSONUtil.toJsonStr(item.get("children")), listForMapTypeReference);
                    List<Map<String, Object>> fieldArrayChildren = new ArrayList<>();
                    for (Map<String, Object> fieldChild : fieldChildren) {
                        Map<String, Object> find = null;
                        for (Map<String, Object> itemChild : itemChildren) {
                            if (fieldChild.get("jsonPath").toString().equals(itemChild.get("jsonPath").toString())) {
                                find = itemChild;
                            }
                        }
                        if (find != null) {
                            mergeValue(fieldChild, find);
                        }
                        fieldArrayChildren.add(fieldChild);
                    }
                    field.put("children", fieldArrayChildren);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }


    }


}
