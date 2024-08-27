package com.hwq.dataloom.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinitionRequest;
import com.hwq.dataloom.model.dto.newdatasource.TableField;
import com.hwq.dataloom.utils.datasource.ExcelUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.util.*;

public class ApiUtils {

    private static String path = "['%s']";

    public static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 请求第三方API获取返回
     * @param apiDefinition
     * @return
     * @throws IOException
     */
    public static CloseableHttpResponse getApiResponse(ApiDefinition apiDefinition) throws IOException {
        ThrowUtils.throwIf(apiDefinition == null, ErrorCode.PARAMS_ERROR, "请求为空");
        ApiDefinitionRequest apiDefinitionRequest = apiDefinition.getRequest();

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpUriRequestBase request = null;

        // 根据请求方法创建对应的请求对象
        switch (apiDefinition.getMethod().toUpperCase()) {
            case "POST":
                request = new HttpPost(apiDefinition.getUrl());
                break;
            case "GET":
                // 构建 GET 请求的URL
                String url = apiDefinition.getUrl();
                if (!apiDefinitionRequest.getArguments().isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder(url);
                    stringBuilder.append("?");
                    for (Map<String, String> argument : apiDefinitionRequest.getArguments()) {
                        for (Map.Entry<String, String> entry : argument.entrySet()) {
                            stringBuilder.append(entry.getKey())
                                    .append("=")
                                    .append(entry.getValue())
                                    .append("&");
                        }
                    }
                    url = stringBuilder.toString().replaceAll("&$", "");
                }
                request = new HttpGet(url);
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(30000))
                .setResponseTimeout(Timeout.ofSeconds(apiDefinition.getApiQueryTimeout() * 1000))
                .build();
        request.setConfig(requestConfig);

        // 设置请求头
        for (Map<String, String> header : apiDefinition.getRequest().getHeaders()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 设置请求体
        if (apiDefinition.getMethod().equalsIgnoreCase("POST")) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = null;
            try {
                jsonBody = objectMapper.writeValueAsString(apiDefinition.getRequest().getBody());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            ((HttpPost) request).setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        }

        // 获取结果
        CloseableHttpResponse response = null;
        response = httpClient.execute(request);
        return response;
    }

    /**
     * 处理接口返回的JSON
     *
     * @param jsonStr
     * @param fields
     * @param rootPath
     */
    public static void handleStr(ApiDefinition apiDefinition,String jsonStr, List<Map<String, Object>> fields, String rootPath) {
        // 用于做类型判断
        ExcelUtils excelUtils = new ExcelUtils();
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
                handleStr(apiDefinition,o.toString(), fields, rootPath);
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
                            handleStr(apiDefinition,node.toString(), childrenField, rootPath + "." + String.format(path, fieldName) + "[*]");
                        }
                        map.put("children", childrenField);
                        map.put("childrenDataType", "LIST");
                    } catch (JsonProcessingException e) {
                        JsonArray array = new JsonArray();
                        array.add(StringUtils.isNotEmpty(value) ? value : "");
                        map.put("value", array);
                    }
                    map.put("jsonPath", rootPath + "." + String.format(path, fieldName));

                    setProperty(apiDefinition,map, fieldName);

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
                        handleStr(apiDefinition,value, children, rootPath + "." + String.format(path, fieldName));
                        map.put("children", children);
                        map.put("childrenDataType", "OBJECT");
                        map.put("jsonPath", rootPath + "." + fieldName);
                    } catch (JsonProcessingException e) {
                        map.put("jsonPath", rootPath + "." + String.format(path, fieldName));
                        JSONArray array = new JSONArray();
                        array.add(StringUtils.isNotEmpty(value) ? value : "");
                        map.put("value", array);
                    }

                    setProperty(apiDefinition,map, fieldName);

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
                    // 识别原始字段类型
                    String type = excelUtils.cellType(StringUtils.isNotEmpty(value) ? value : "");
                    map.put("type", type);
                    setProperty(apiDefinition,map, fieldName);

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
    public static void setProperty(ApiDefinition apiDefinition,Map<String, Object> map, String s) {
        map.put("originName", s);
        map.put("name", s);
        if (ObjectUtils.isEmpty(map.get("type"))) {
            map.put("type", "TEXT");
        }
        map.put("size", 65535);
        map.put("deExtractType", 0);
        map.put("deType", 0);
        map.put("checked", false);
        if (!apiDefinition.isUseJsonPath()) {
            for (TableField field : apiDefinition.getFields()) {
                if (!ObjectUtils.isEmpty(map.get("jsonPath")) && StringUtils.isNotEmpty(field.getJsonPath()) && field.getJsonPath().equals(map.get("jsonPath").toString())) {
                    map.put("checked", true);
                    map.put("name", field.getName());
                }
            }
        }
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

    public static List<String[]> toDataList(String jsonString) {
        // 使用Hutool的JSON工具类解析字符串为JSONObject
        JSONObject jsonObject = JSONUtil.parseObj(jsonString);
        JSONArray jsonFields = jsonObject.getJSONArray("jsonFields");

        // 存储提取的数据
        List<String[]> extractedData = new ArrayList<>();

        // 提取顶层的value
        List<String[]> topValues = new ArrayList<>();
        for (Object obj : jsonFields) {
            JSONObject field = (JSONObject) obj;
            if (field.containsKey("value")) {
                JSONArray values = field.getJSONArray("value");
                topValues.add(values.toArray(new String[0]));
            }
        }

        // 提取子元素的value
        for (Object obj : jsonFields) {
            JSONObject field = (JSONObject) obj;
            if (field.containsKey("children")) {
                JSONArray children = field.getJSONArray("children");
                List<String[]> childrenValues = new ArrayList<>();
                for (Object childObj : children) {
                    JSONObject child = (JSONObject) childObj;
                    JSONArray values = child.getJSONArray("value");
                    childrenValues.add(values.toArray(new String[0]));
                }

                // 组合子元素的value
                for (int k = 0; k < childrenValues.get(0).length; k++) {
                    List<String> combinedValues = new ArrayList<>();
                    for (String[] childValue : childrenValues) {
                        combinedValues.add(childValue[k]);
                    }
                    extractedData.add(combinedValues.toArray(new String[0]));
                }
            }
        }

        // 将顶层和子元素的value合并
        List<String[]> finalResult = new ArrayList<>();
        for (String[] topValue : topValues) {
            for (String[] combinedValue : extractedData) {
                String[] fullRecord = new String[topValue.length + combinedValue.length];
                System.arraycopy(topValue, 0, fullRecord, 0, topValue.length);
                System.arraycopy(combinedValue, 0, fullRecord, topValue.length, combinedValue.length);
                finalResult.add(fullRecord);
            }
        }

        return finalResult;
    }


}
