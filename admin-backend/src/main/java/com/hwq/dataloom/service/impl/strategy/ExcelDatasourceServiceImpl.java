package com.hwq.dataloom.service.impl.strategy;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hwq.dataloom.constant.DatasourceConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.ai.AskAIWithDataTablesAndFieldsRequest;
import com.hwq.dataloom.model.dto.datasource.TableFieldInfo;
import com.hwq.dataloom.model.dto.datasource_tree.AddDatasourceDirRequest;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.model.enums.DataSourceTypeEnum;
import com.hwq.dataloom.model.enums.DirTypeEnum;
import com.hwq.dataloom.model.json.ai.UserChatForSQLRes;
import com.hwq.dataloom.model.json.datasource.ExcelSheetData;
import com.hwq.dataloom.model.vo.data.QueryAICustomSQLVO;
import com.hwq.dataloom.service.CoreDatasetTableFieldService;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.DatasourceDirTreeService;
import com.hwq.dataloom.service.basic.strategy.DatasourceExecuteStrategy;
import com.hwq.dataloom.utils.datasource.CustomPage;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import com.hwq.dataloom.utils.datasource.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author HWQ
 * @date 2024/8/25 00:42
 * @description
 */
@Service
public class ExcelDatasourceServiceImpl implements DatasourceExecuteStrategy<DatasourceDTO> {

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private DatasourceDirTreeService datasourceDirTreeService;

    @Resource
    private CoreDatasetTableFieldService coreDatasetTableFieldService;

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @Resource
    private ExcelUtils excelUtils;
    @Autowired
    private DatasourceEngine datasourceEngine;

    @Override
    public String mark() {
        return DataSourceTypeEnum.EXCEL.getValue();
    }

    @Override
    public CoreDatasource getCoreDatasource() {
        return null;
    }

    @Override
    public Long addCoreData(DatasourceDTO datasourceDTO, User loginUser) {
        // 1.根据pid存储到数据源文件树中
        AddDatasourceDirRequest addDatasourceDirRequest = AddDatasourceDirRequest
                .builder()
                .name(datasourceDTO.getName())
                .pid(datasourceDTO.getPid())
                .type(DirTypeEnum.FILE.getText())
                .build();
        datasourceDirTreeService.addDatasourceDirNode(addDatasourceDirRequest, loginUser);
        // 2.存储数据源信息
        CoreDatasource coreDatasource = new CoreDatasource();
        coreDatasource.setName(datasourceDTO.getName());
        coreDatasource.setDescription(datasourceDTO.getDescription());
        coreDatasource.setConfiguration(datasourceDTO.getConfiguration());
        coreDatasource.setType(datasourceDTO.getType());
        coreDatasource.setUserId(loginUser.getId());
        ThrowUtils.throwIf(!coreDatasourceService.save(coreDatasource), ErrorCode.SYSTEM_ERROR);
        MultipartFile multipartFile = datasourceDTO.getMultipartFile();
        // 3.校验文件
        checkFileValid(multipartFile);
        // 4. 将数据源解析数据分别写入数据库中
        Long coreDatasourceId = coreDatasource.getId();
        try {
            InputStream inputStream = multipartFile.getInputStream();
            // 4.1 保存数据源数据并返回解析信息
            List<ExcelSheetData> excelSheetData = excelUtils.parseAndSaveFile(coreDatasourceId, multipartFile.getOriginalFilename(), inputStream);
            for (ExcelSheetData excelSheet : excelSheetData) {
                // 4.2 创建表信息
                CoreDatasetTable coreDatasetTable = new CoreDatasetTable();
                coreDatasetTable.setName(excelSheet.getSheetName());
                // 4.3 设置表名excel_{datasourceId}_sheetName
                coreDatasetTable.setTableName(String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.EXCEL.getValue(), coreDatasourceId, excelSheet.getSheetName()));
                coreDatasetTable.setDatasourceId(coreDatasourceId);
                coreDatasetTable.setType(DataSourceTypeEnum.EXCEL.getValue());
                Long datasetTableId = coreDatasetTableService.addDatasetTable(coreDatasetTable);
                // 4.4 创建字段信息
                List<TableFieldInfo> fieldInfos = excelSheet.getFieldInfos();
                AtomicInteger index = new AtomicInteger();
                List<CoreDatasetTableField> coreDatasetTableFieldList = fieldInfos
                        .stream()
                        .map(field -> {
                            CoreDatasetTableField coreDatasetTableField = new CoreDatasetTableField();
                            coreDatasetTableField.setDatasourceId(coreDatasourceId);
                            coreDatasetTableField.setDatasetTableId(datasetTableId);
                            coreDatasetTableField.setOriginName(field.getOriginName());
                            coreDatasetTableField.setName(field.getName());
                            coreDatasetTableField.setType(field.getFieldType());
                            coreDatasetTableField.setColumnIndex(index.getAndIncrement());
                            return coreDatasetTableField;
                        }).collect(Collectors.toList());
                ThrowUtils.throwIf(!coreDatasetTableFieldService.saveBatch(coreDatasetTableFieldList), ErrorCode.SYSTEM_ERROR, "批量插入字段失败");
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件异常");
        }
        return coreDatasourceId;
    }

    @Override
    public Boolean validDatasource(DatasourceDTO datasourceDTO) {
        // 校验文件
        MultipartFile multipartFile = datasourceDTO.getMultipartFile();
        return checkFileValid(multipartFile);
    }

    @Override
    public List<CoreDatasetTable> getTables(CoreDatasource coreDatasource) {
        ThrowUtils.throwIf(coreDatasource == null || coreDatasource.getId() == null, ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<CoreDatasetTable> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(CoreDatasetTable::getDatasourceId, coreDatasource.getId());
        return coreDatasetTableService.list(lambdaQueryWrapper);
    }

    @Override
    public List<CoreDatasetTableField> getTableFields(CoreDatasource coreDatasource, String tableName) {
        ThrowUtils.throwIf(coreDatasource == null || coreDatasource.getId() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(tableName), ErrorCode.PARAMS_ERROR);
        Long datasourceId = coreDatasource.getId();
        // 1. 找出对应的表名的数据源表信息
        LambdaQueryWrapper<CoreDatasetTable> lqw = new LambdaQueryWrapper<>();
        lqw
                .eq(CoreDatasetTable::getDatasourceId, datasourceId)
                .eq(CoreDatasetTable::getTableName, tableName);
        CoreDatasetTable coreDatasetTable = coreDatasetTableService.getOne(lqw);
        ThrowUtils.throwIf(coreDatasetTable == null, ErrorCode.NOT_FOUND_ERROR);
        // 2. 查询表字段信息
        LambdaQueryWrapper<CoreDatasetTableField> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(CoreDatasetTableField::getDatasourceId, datasourceId)
                .eq(CoreDatasetTableField::getDatasetTableId, coreDatasetTable.getId());
        return coreDatasetTableFieldService.list(lambdaQueryWrapper);
    }

    @Override
    public CustomPage<Map<String, Object>> getDataFromDatasourceBySql(CoreDatasource datasource, String sql, Integer pageNo, Integer pageSize) throws SQLException {
        return datasourceEngine.execSelectSqlToQueryAICustomSQLVO(datasource.getId(), sql, pageNo,pageSize);
    }

    @Override
    public List<AskAIWithDataTablesAndFieldsRequest> getAskAIWithDataTablesAndFieldsRequests(CoreDatasource coreDatasource, User loginUser) {
        // 获取对应数据源所有表信息
        List<CoreDatasetTable> tables = coreDatasourceService.getTablesByDatasourceId(coreDatasource.getId(), loginUser);
        ThrowUtils.throwIf(tables.isEmpty(), ErrorCode.PARAMS_ERROR, "数据源暂无数据");
        List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests = new ArrayList<>();
        tables.forEach(table -> {
            // 查询所有字段
            LambdaQueryWrapper<CoreDatasetTableField> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CoreDatasetTableField::getDatasetTableId, table.getId());
            List<CoreDatasetTableField> tableFields = coreDatasetTableFieldService.list(wrapper);
            AskAIWithDataTablesAndFieldsRequest askAIWithDataTablesAndFieldsRequest = AskAIWithDataTablesAndFieldsRequest.builder()
                    .tableId(table.getId())
                    .tableComment(table.getName())
                    .tableName(table.getTableName())
                    .coreDatasetTableFieldList(tableFields)
                    .build();
            dataTablesAndFieldsRequests.add(askAIWithDataTablesAndFieldsRequest);
        });
        return dataTablesAndFieldsRequests;
    }

    /**
     * 校验文件
     * @param multipartFile
     */
    private Boolean checkFileValid(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();
        // 校验文件大小
        final long ONE_MB = 10 * 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 10M");
        // 校验文件后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls", "csv");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
        return true;
    }
}