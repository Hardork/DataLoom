package com.hwq.dataloom.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class ApiUtils {

    private static String path = "['%s']";

    public static ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 处理接口返回的JSON
     * @param jsonStr
     * @param fields
     * @param rootPath
     */
    public static void handleStr(String jsonStr, List<Map<String,Object>> fields, String rootPath) {
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
                handleStr(o.toString(),fields,rootPath);
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
                        List<Map<String,Object>> childrenField = new ArrayList<>();
                        for (JsonNode node : jsonArray) {
                            if (StringUtils.isNotEmpty(node.toString()) && !node.toString().startsWith("[") && !node.toString().startsWith("{")) {
                                throw new BusinessException(ErrorCode.PARAMS_ERROR,"不为JSON格式");
                            }
                        }
                        for (JsonNode node : jsonArray) {
                            handleStr(node.toString(),childrenField,rootPath + "." + String.format(path,fieldName) + "[*]");
                        }
                        map.put("children", childrenField);
                        map.put("childrenDataType", "LIST");
                    } catch (JsonProcessingException e) {
                        JsonArray array = new JsonArray();
                        array.add(StringUtils.isNotEmpty(value) ? value : "");
                        map.put("value", array);
                    }
                    map.put("jsonPath", rootPath + "." + String.format(path, fieldName));

                    setProperty(map,fieldName);

                    // TODO 检测是否有相同的字段进行合并 增强代码的健壮性
                    fields.add(map);


                } else if (StringUtils.isNotEmpty(value) && value.startsWith("{")) {
                    // 处理JSON对象字段

                } else {
                    // 处理普通类型的字段

                }
            }
        }
    }


    /**
     * 设置TableField属性
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







}
