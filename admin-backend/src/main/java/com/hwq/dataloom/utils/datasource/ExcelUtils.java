package com.hwq.dataloom.utils.datasource;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.constant.UserDataConstant;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.mapper.ChartMapper;
import com.hwq.dataloom.model.dto.datasource.TableFieldInfo;
import com.hwq.dataloom.model.vo.data.PreviewExcelDataVO;
import com.hwq.dataloom.mongo.entity.ChartData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel 相关工具类
 */
@Slf4j
@Component
public class ExcelUtils {

    /**
     * excel 转 csv
     *
     * @param multipartFile
     * @return
     */

    @Resource
    private ChartMapper chartMapper;

    @Resource
    private MongoTemplate mongoTemplate;



    public String excelToCsv(MultipartFile multipartFile) {
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误", e);
        }
        if (CollUtil.isEmpty(list)) {
            return "";
        }
        // 转换为 csv
        StringBuilder stringBuilder = new StringBuilder();
        // 读取表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        // 表头数据
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
        // 读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
        return stringBuilder.toString();
    }


    /**
     * 将用户上传的Excel保存到Mongo中
     * @param multipartFile
     * @param id
     */
    public List<TableFieldInfo> saveDataToMongo(MultipartFile multipartFile, Long id) {
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误", e);
        }
        if (CollUtil.isEmpty(list)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "表格数据为空");
        }
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        // 取出表头
        List<String> headerList = new ArrayList<>(headerMap.values());
        // 校验表头是否包含特殊字符
        MongoDBColumnValidator.validateColumnNames(headerList);
        // 存储字段头
        List<TableFieldInfo> fields = new ArrayList<>();
        // 初始化表头字段
        for (String s : headerList) {
            TableFieldInfo tableFiled = new TableFieldInfo();
            tableFiled.setFieldType(null);
            tableFiled.setName(s);
            tableFiled.setOriginName(s);
            fields.add(tableFiled);
        }

        // 统计字数
        try {
            List<ChartData> insertData = new ArrayList<>();
            // 获取封装后的数据
            for (int i = 1; i < list.size(); i++) {
                LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i); // [1, 2]
                // dataList存储当前行所有列的数据
                List<String> dataList = new ArrayList<>(dataMap.values());
                // 校验数据类型
                // 遍历每一个元素
                for (int j = 0; j < dataList.size(); j++) {
                    if (j < headerList.size()) {
                        cellType(dataList.get(j), fields.get(j));
                    }
                }
                ChartData chartData = getDataMap(headerList, dataList);
                if (chartData != null) insertData.add(chartData);
            }
            // 插入数据
            insertMongoDB(id, insertData);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请根据文件规范，检查上传文件是否正常");
        }
        return fields;
    }


    public Long saveDataToMongo(InputStream inputStream, Long id) {
        // 读取数据
        List<Map<Integer, String>> list = null;
        list = EasyExcel.read(inputStream)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .headRowNumber(0)
                .doReadSync();
        if (CollUtil.isEmpty(list)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "表格数据为空");
        }
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        // 取出表头
        List<String> headerList = new ArrayList<>(headerMap.values());

        // 统计字数
            List<ChartData> insertData = new ArrayList<>();
            // 获取封装后的数据
            for (int i = 1; i < list.size(); i++) {
                LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
                // dataList存储当前行所有列的数据
                List<String> dataList = new ArrayList<>(dataMap.values());
                ChartData chartData = getDataMap(headerList, dataList);
                if (chartData != null) insertData.add(chartData);
            }
            // 插入数据
            insertMongoDB(id, insertData);
        return id;
    }

    /**
     * 类型推断
     *
     * @param value
     * @param tableFieldInfo
     * @return
     */
    public void cellType(String value, TableFieldInfo tableFieldInfo) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        String type = cellType(value);
        if (tableFieldInfo.getFieldType() == null) {
            tableFieldInfo.setFieldType(type);
        } else {
            if (type.equalsIgnoreCase("TEXT")) {
                tableFieldInfo.setFieldType(type);
            }
            if (type.equalsIgnoreCase("DOUBLE") && tableFieldInfo.getFieldType().equalsIgnoreCase("LONG")) {
                tableFieldInfo.setFieldType(type);
            }
        }

    }

    public String cellType(String value) {
        if( value.length() > 19){
            return "TEXT";
        }
        if (DateTimeValidator.isDateTime(value)) {
            return "DATETIME";
        }
        try {
            Double d = Double.valueOf(value);
            double eps = 1e-10;
            if (d - Math.floor(d) < eps) {
                return "BIGINT";
            } else {
                return "DOUBLE";
            }
        } catch (Exception e2) {
            return "TEXT";
        }
    }


    /**
     * 封装一行的数据成ChartData
     * @param header 标题行
     * @param data 数据行
     * @return
     */
    public ChartData getDataMap(List<String> header, List<String> data) {
        if (header.size() != data.size()) {
            return null;
        }
        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            map.put(header.get(i), data.get(i));
        }
        return new ChartData()
                .setData(map);
    }

    public void insertMongoDB(Long chartId, List<ChartData> chartDataList) {
        mongoTemplate.insert(chartDataList, "chart_" + chartId);
    }

    /**
     * 将mongo中的数据转为CSV格式（节约token）
     * @param chartId
     * @return
     */
    public String mongoToCSV(Long chartId) {
        List<ChartData> dataList = mongoTemplate.findAll(ChartData.class, UserDataConstant.USER_CHART_DATA_PREFIX + chartId);
        if (dataList.isEmpty()) {
            return "";
        }
        // 获取表头
        ChartData chartData = dataList.get(0);
        Map<String, Object> data = chartData.getData();
        List<String> headerList = new ArrayList<>(data.keySet());
        StringBuilder csvData = new StringBuilder();
        csvData.append(StringUtils.join(headerList, ",")).append("\n");
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> curData = dataList.get(i).getData();
            for (int j = 0; j < headerList.size(); j++) {
                if (j == headerList.size() - 1) {
                    csvData.append(curData.get(headerList.get(j)));
                } else {
                    csvData.append(curData.get(headerList.get(j)));
                    csvData.append(",");
                }
            }
            csvData.append("\n");
        }
        return csvData.toString();

    }

    public void saveDataToMySQL(InputStream inputStream, Long id, List<TableFieldInfo> fields) {
        // 读取数据
        List<Map<Integer, String>> list = null;
        list = EasyExcel.read(inputStream)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .headRowNumber(0)
                .doReadSync();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        // 获取表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        // 取出表头
        List<String> headerList = new ArrayList<>(headerMap.values());
        // 校验表头是否包含特殊字符
        MongoDBColumnValidator.validateColumnNames(headerList);
        // 校验表头的匹配度
        try {
            // 生成数据表
            chartMapper.genChartDataTable(id, fields);
            // 读取数据
            for (int i = 1; i < list.size(); i++) {
                LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
                // 插入数据
                List<String> dataList = new ArrayList<>();
                for (int j = 0; j < fields.size() && j < dataMap.size(); j++) {
                    if (checkSinglePropertiesValid(dataMap.get(j), fields.get(j).getFieldType())) {
                        dataList.add(dataMap.get(j));
                    } else {
                        dataList.add(null);
                    }
                }
                chartMapper.insertDataToChartDataTable(id, headerList, dataList);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            // 判断表是否已经创建，创建了进行删除
            chartMapper.DropTableAfterException(id);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请根据文件规范，检查上传文件是否正常,列字段不能包含特殊字符");
        }
    }

    public boolean checkSinglePropertiesValid(String properties, String target) {
        if (StringUtils.isEmpty(properties)) {
            return false;
        }
        if (target.equals("TEXT")) {
            return true;
        }
        String cur = cellType(properties);
        return cur.equals(target);
    }

    /**
     * 查询上传文件对应字段类型
     * @param multipartFile
     * @return
     */
    public PreviewExcelDataVO queryDataFields(MultipartFile multipartFile) {
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误", e);
        }
        if (CollUtil.isEmpty(list)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "表格数据为空");
        }
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        // 取出表头
        List<String> headerList = new ArrayList<>(headerMap.values());
        // 结果类
        PreviewExcelDataVO dataPage = new PreviewExcelDataVO();

        // 校验表头是否包含特殊字符
        if(!MongoDBColumnValidator.validateColumnNames(headerList)) {
            dataPage.setIsValid(false);
            dataPage.setErrorMessage("列名中不得包含特殊字符：" + MongoDBColumnValidator.SPECIAL_CHARACTERS);
        }
        // 存储字段头
        List<TableFieldInfo> fields = new ArrayList<>();
        // 初始化表头字段
        for (String s : headerList) {
            TableFieldInfo tableFiled = new TableFieldInfo();
            tableFiled.setFieldType(null);
            tableFiled.setName(s);
            tableFiled.setOriginName(s);
            fields.add(tableFiled);
        }
        List<ChartData> previewDataList = new ArrayList<>();
        // 统计字数
        try {
            // 取出前5行作为预览数据
            for (int i = 1; i < 6 && i < list.size(); i++) {
                LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i); // [1, 2]
                // dataList存储当前行所有列的数据
                List<String> dataList = new ArrayList<>(dataMap.values());
                ChartData chartData = new ChartData();
                Map<String, Object> data = new HashMap<>();
                // 遍历每一个元素
                for (int j = 0; j < dataList.size(); j++) {
                    if (j < headerList.size()) {
                        data.put(headerList.get(j), dataList.get(j));
                    }
                }
                chartData.setData(data);
                previewDataList.add(chartData);
            }
            // 只校验前200行数据
            for (int i = 1; i < list.size() && i < 200; i++) {
                LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i); // [1, 2]
                // dataList存储当前行所有列的数据
                List<String> dataList = new ArrayList<>(dataMap.values());
                // 校验数据类型
                // 遍历每一个元素
                for (int j = 0; j < dataList.size(); j++) {
                    if (j < headerList.size()) {
                        cellType(dataList.get(j), fields.get(j));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请根据文件规范，检查上传文件是否正常");
        }
        dataPage.setTableFieldInfosList(fields);
        dataPage.setDataList(previewDataList);
        return dataPage;
    }

    /**
     * 将用户上传的Excel存储到MySQL中
     * @param multipartFile
     * @param id
     */
    @Transactional
    public void saveDataToMySQL(MultipartFile multipartFile, Long id) {
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误", e);
        }
        if (CollUtil.isEmpty(list)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "表格数据为空");
        }
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        // 取出表头
        List<String> headerList = new ArrayList<>(headerMap.values());
        // 校验表头是否包含特殊字符
        MongoDBColumnValidator.validateColumnNames(headerList);
        // 存储字段头
        List<TableFieldInfo> fields = new ArrayList<>();
        // 初始化表头字段
        for (String s : headerList) {
            TableFieldInfo tableFiled = new TableFieldInfo();
            tableFiled.setFieldType(null);
            tableFiled.setName(s);
            tableFiled.setOriginName(s);
            fields.add(tableFiled);
        }
        try {
            // 生成数据表
            chartMapper.genChartDataTable(id, fields);
            // 读取数据
            for (int i = 1; i < list.size(); i++) {
                LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
                // 插入数据
                List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
                chartMapper.insertDataToChartDataTable(id, headerList, dataList);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            // 判断表是否已经创建，创建了进行删除
            chartMapper.DropTableAfterException(id);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请根据文件规范，检查上传文件是否正常,列字段不能包含特殊字符");
        }
    }
}
