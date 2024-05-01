package com.hwq.bi.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.constant.ChartConstant;
import com.hwq.bi.constant.UserDataConstant;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.mapper.ChartMapper;
import com.hwq.bi.mongo.entity.ChartData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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


    public void saveDataToMongo(MultipartFile multipartFile, Long id) {
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
            return;
        }
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);// ["日期", "字符串", "小树"]
        // 创建表
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).map(s -> s.replace(".", "_")).
                collect(Collectors.toList());

        try {
            List<ChartData> insertData = new ArrayList<>();
            // 获取封装后的数据
            for (int i = 1; i < list.size(); i++) {
                LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i); // [1, 2]
                List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
                ChartData chartData = getDataMap(headerList, dataList);
                if (chartData != null) insertData.add(chartData);
            }
            // 插入数据
            insertMongoDB(id, insertData);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请根据文件规范，检查上传文件是否正常");
        }
    }

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
            return;
        }
        // 读取表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);// ["日期", "字符串", "小树"]
        // 创建表
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        try {
            chartMapper.genChartDataTable(id, headerList);
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请根据文件规范，检查上传文件是否正常");
        }
    }



}
