package com.hwq.dataloom.utils.datasource;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.hwq.dataloom.config.CommonThreadPool;
import com.hwq.dataloom.constant.DatasourceConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.constant.UserDataConstant;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.mapper.ChartMapper;
import com.hwq.dataloom.model.dto.datasource.TableFieldInfo;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.enums.DataSourceTypeEnum;
import com.hwq.dataloom.model.enums.TableFieldTypeEnum;
import com.hwq.dataloom.model.json.ExcelSheetData;
import com.hwq.dataloom.model.vo.data.PreviewExcelDataVO;
import com.hwq.dataloom.mongo.entity.ChartData;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private DatasourceEngine datasourceEngine;

    @Resource
    private CommonThreadPool commonThreadPool;

    public static final String UFEFF = "\uFEFF";


    /**
     * 表字段类型推断-外层
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
            if (type.equalsIgnoreCase(TableFieldTypeEnum.TEXT.getValue())) {
                tableFieldInfo.setFieldType(TableFieldTypeEnum.TEXT.getValue());
            }
            if (type.equalsIgnoreCase(TableFieldTypeEnum.DOUBLE.getValue()) && tableFieldInfo.getFieldType().equalsIgnoreCase(TableFieldTypeEnum.BIGINT.getValue())) {
                tableFieldInfo.setFieldType(TableFieldTypeEnum.DOUBLE.getValue());
            }
        }
    }

    /**
     * 表字段类型推断
     * @param value
     * @return
     */
    public String cellType(String value) {
        if(value.length() > 19){
            return TableFieldTypeEnum.TEXT.getValue();
        }
        if (DateTimeValidator.isDateTime(value)) {
            return TableFieldTypeEnum.DATETIME.getValue();
        }
        try {
            Double d = Double.valueOf(value);
            double eps = 1e-10;
            if (d - Math.floor(d) < eps) {
                return TableFieldTypeEnum.BIGINT.getValue();
            } else {
                return TableFieldTypeEnum.DOUBLE.getValue();
            }
        } catch (Exception e2) {
            return TableFieldTypeEnum.TEXT.getValue();
        }
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
        DBColumnValidator.validateColumnNames(headerList);
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
        DBColumnValidator.validateColumnNames(headerList);
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
                List<String> dataList = dataMap.values()
                        .stream()
                        .filter(ObjectUtils::isNotEmpty)
                        .collect(Collectors.toList());
                chartMapper.insertDataToChartDataTable(id, headerList, dataList);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            // 判断表是否已经创建，创建了进行删除
            chartMapper.DropTableAfterException(id);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请根据文件规范，检查上传文件是否正常,列字段不能包含特殊字符");
        }
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
        if(!DBColumnValidator.validateColumnNames(headerList)) {
            dataPage.setIsValid(false);
            dataPage.setErrorMessage("列名中不得包含特殊字符：" + DBColumnValidator.SPECIAL_CHARACTERS);
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
     * 插入数据数据库中
     * @param datasourceId 数据源id
     * @param dataList 行数据列表
     * @param tableName 表名
     */
    private void insertExcelData(Long datasourceId, List<String[]> dataList, String tableName) {
        int pageNumber = 1000; //一次插入 1000条
        int totalPage;
        if (dataList.size() % pageNumber > 0) {
            totalPage = dataList.size() / pageNumber + 1;
        } else {
            totalPage = dataList.size() / pageNumber;
        }
        for (int page = 1; page <= totalPage; page++) {
            datasourceEngine.execInsert(datasourceId, tableName, dataList, page, pageNumber);
        }
    }


    @Data
    public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {
        private List<String[]> data = new ArrayList<>();
        private List<String> header = new ArrayList<>();
        private List<Integer> headerKey = new ArrayList<>();

        @Override
        public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
            super.invokeHead(headMap, context);
            for (Integer key : headMap.keySet()) {
                ReadCellData<?> cellData = headMap.get(key);
                String value = cellData.getStringValue();
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                headerKey.add(key);
                header.add(value);
            }
        }

        @Override
        public void invoke(Map<Integer, String> dataMap, AnalysisContext context) {
            List<String> line = new ArrayList<>();
            for (Integer key : dataMap.keySet()) {
                String value = dataMap.get(key);
                if (StringUtils.isEmpty(value)) {
                    value = null;
                }
                if (headerKey.contains(key)) {
                    line.add(value);
                }
            }
            int size = line.size();
            if (size < header.size()) {
                for (int i = 0; i < header.size() - size; i++) {
                    line.add(null);
                }
            }
            data.add(line.toArray(new String[line.size()]));
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        }

        public void clear() {
            data.clear();
            header.clear();
        }
    }

    /**
     * 保存并解析文件
     * @param datasourceId 数据源id
     * @param filename 文件名
     * @param inputStream 输入流
     * @return 配置信息
     */
    public List<ExcelSheetData> parseAndSaveFile(Long datasourceId, String filename, InputStream inputStream) {
        List<ExcelSheetData> excelSheetData = parseFile(filename, inputStream, false);
        // 创建表
        for (ExcelSheetData excelSheet : excelSheetData) {
            List<TableFieldInfo> fieldInfos = excelSheet.getFieldInfos();
            String tableName = String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.EXCEL.getValue(), datasourceId, excelSheet.getSheetName());
            List<CoreDatasetTableField> tableFields = fieldInfos
                    .stream()
                    .map(field -> {
                        CoreDatasetTableField datasetTableField = new CoreDatasetTableField();
                        datasetTableField.setName(field.getName());
                        datasetTableField.setOriginName(field.getOriginName());
                        datasetTableField.setType(field.getFieldType());
                        return datasetTableField;
                    })
                    .collect(Collectors.toList());
            try {
                datasourceEngine.exeCreateTable(datasourceId, tableName, tableFields);
            } catch (Exception e) {
                // 删除数据源信息，并返回异常
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建表失败");
            }
            // 线程池，异步插入数据
            commonThreadPool.addTask(() -> {
                insertExcelData(datasourceId, excelSheet.getData(), tableName);
            });
            // 设置配置，无需数据部分
            excelSheet.setJsonArray(null);
        }

        return excelSheetData;
    }

    /**
     * 解析文件数据
     * @param filename 文件名
     * @param inputStream 文件流
     * @param isPreview 是否为预览阶段 预览阶段只返回100条数据
     * @return
     */
    @SneakyThrows
    public List<ExcelSheetData> parseFile(String filename, InputStream inputStream, boolean isPreview) {
        List<ExcelSheetData> fileDataList = new ArrayList<>();
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        if (StringUtils.equalsIgnoreCase(suffix, "xlsx") || StringUtils.equalsIgnoreCase(suffix, "xls")) { // excel
            handleExcel(filename, inputStream, isPreview, fileDataList);
        }
        if (StringUtils.equalsIgnoreCase(suffix, "csv")) { // csv
            handleCsv(filename, inputStream, isPreview, fileDataList);
        }
        for (ExcelSheetData excelSheetData : fileDataList) {
            List<String[]> data = excelSheetData.getData();
            String[] fieldArray = excelSheetData.getFieldInfos().stream().map(TableFieldInfo::getName).toArray(String[]::new);
            List<Map<String, Object>> jsonArray = new ArrayList<>();
            if (data != null) {
                jsonArray = data.stream().map(ele -> {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < fieldArray.length; i++) {
                        map.put(fieldArray[i], i < ele.length ? ele[i] : "");
                    }
                    return map;
                }).collect(Collectors.toList());
            }
            excelSheetData.setJsonArray(jsonArray);
        }
        return fileDataList;
    }

    /**
     * 解析Excel文件
     * @param fileName 文件名
     * @param inputStream 文件流
     * @param isPreview 是否预览
     * @param excelSheetDataList 存储数据集合
     */
    private void handleExcel(String fileName, InputStream inputStream, boolean isPreview, List<ExcelSheetData> excelSheetDataList) {
        // 使用 EasyExcel 读取文件
        NoModelDataListener noModelDataListener = new NoModelDataListener();
        ExcelReaderBuilder read = EasyExcel.read(inputStream, noModelDataListener);
        ExcelReader excelReader = read.build();
        try {
            // 获取所有 sheet
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            // 遍历每个 sheet
            for (ReadSheet sheet : sheets) {
                noModelDataListener.clear();
                excelReader.read(sheet);
                ExcelSheetData excelSheetData = new ExcelSheetData();
                excelSheetData.setFileName(fileName);
                excelSheetData.setSheetName(sheet.getSheetName());
                // 取出表头
                List<String> headerList = new ArrayList<>(noModelDataListener.getHeader());

                // 用于存储所有 Sheet 的字段信息和预览数据
                List<TableFieldInfo> fields = new ArrayList<>();

                // 校验表头是否包含特殊字符
                ThrowUtils.throwIf(!DBColumnValidator.validateColumnNames(headerList), ErrorCode.OPERATION_ERROR, "列名中不得包含特殊字符：" + DBColumnValidator.SPECIAL_CHARACTERS);

                // 初始化表头字段
                for (String s : headerList) {
                    TableFieldInfo tableFiled = new TableFieldInfo();
                    tableFiled.setFieldType(null);
                    tableFiled.setName(s);
                    tableFiled.setOriginName(s);
                    fields.add(tableFiled);
                }
                // 设置字段信息
                excelSheetData.setFieldInfos(fields);
                // 数据部分
                List<String[]> data = new ArrayList<>(noModelDataListener.getData());
                // 进行预览
                try {
                    // 遍历每一行
                    for (int i = 0; i < data.size(); i++) {
                        // 遍历每一列
                        for (int j = 0; j < data.get(i).length; j++) {
                            // 分析字段
                            if (j < fields.size()) {
                                cellType(data.get(i)[j], fields.get(j));
                            }
                        }
                    }
                    // 取出数据部分
                    if (isPreview) {
                        // 只展示100行
                        if (data.size() > 100) {
                            data = data.subList(0, 100);
                        }
                    }
                    // 设置数据
                    excelSheetData.setData(data);
                    // 设置数据
                    excelSheetDataList.add(excelSheetData);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "请根据文件规范，检查上传文件是否正常");
                }
            }
        } catch (Exception e) {
            log.error("表格处理错误", e);
        } finally {
            excelReader.finish();
        }

    }

    /**
     * 解析CSV文件
     * @param filename 文件名
     * @param inputStream 文件流
     * @param isPreview 是否预览
     * @param excelSheetDataList 存储数据集合
     */
    private void handleCsv(String filename, InputStream inputStream, boolean isPreview, List<ExcelSheetData> excelSheetDataList) throws IOException {
        List<TableFieldInfo> fields = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String s = reader.readLine();// first line
        String[] split = s.split(",");
        for (int i = 0; i < split.length; i++) {
            String filedName = split[i];
            ThrowUtils.throwIf(StringUtils.isEmpty(filedName), ErrorCode.PARAMS_ERROR, "首行行中不允许有空单元格！");
            if (filedName.startsWith(UFEFF)) {
                filedName = filedName.replace(UFEFF, "");
            }
            TableFieldInfo tableFiled = new TableFieldInfo();
            tableFiled.setName(filedName);
            tableFiled.setOriginName(filedName);
            tableFiled.setFieldType(null);
            fields.add(tableFiled);
        }

        List<String[]> data = csvData(reader, isPreview, fields.size());
        if (isPreview) {
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < data.get(i).length; j++) {
                    if (j < fields.size()) {
                        cellType(data.get(i)[j], fields.get(j));
                    }
                }
            }
            if (data.size() > 100) {
                data = data.subList(0, 100);
            }
        }
        for (int i = 0; i < fields.size(); i++) {
            if (StringUtils.isEmpty(fields.get(i).getFieldType())) {
                fields.get(i).setFieldType("TEXT");
            }
        }
        // 设置sheet信息
        ExcelSheetData excelSheetData = new ExcelSheetData();
        excelSheetData.setFieldInfos(fields);
        excelSheetData.setData(data);
        excelSheetData.setFileName(filename);
        excelSheetData.setSheetName(filename.substring(0, filename.lastIndexOf('.')));
        excelSheetDataList.add(excelSheetData);
        // 关闭流
        inputStream.close();
    }

    /**
     * 读取csv文件数据
     * @param reader
     * @param isPreview
     * @param size
     * @return
     */
    public List<String[]> csvData(BufferedReader reader, boolean isPreview, int size) {
        List<String[]> data = new ArrayList<>();
        try {
            int num = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                String str;
                line += ",";
                Pattern pCells = Pattern.compile("(\"[^\"]*(\"{2})*[^\"]*\")*[^,]*,");
                Matcher mCells = pCells.matcher(line);
                List<String> cells = new ArrayList<>();//每行记录一个list
                //读取每个单元格
                while (mCells.find()) {
                    str = mCells.group();
                    str = str.replaceAll("(?sm)\"?([^\"]*(\"{2})*[^\"]*)\"?.*,", "$1");
                    str = str.replaceAll("(?sm)(\"(\"))", "$2");
                    cells.add(str);
                }
                if (!cells.isEmpty()) {
                    if(cells.size() > size){
                        cells = cells.subList(0, size);
                    }
                    data.add(cells.toArray(new String[]{}));
                    num++;
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
        return data;
    }

}
