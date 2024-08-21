package com.hwq.dataloom.service.impl.strategy;
import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.datasource_tree.AddDatasourceDirRequest;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.model.enums.DataSourceTypeEnum;
import com.hwq.dataloom.model.enums.DirTypeEnum;
import com.hwq.dataloom.model.json.StructDatabaseConfiguration;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.DatasourceDirTreeService;
import com.hwq.dataloom.service.basic.DatasourceExecuteStrategy;
import com.hwq.dataloom.utils.datasource.MySQLUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/8/20 23:49
 * @description MySQL数据源策略实现类
 */
public class MySQLDatasourceServiceImpl implements DatasourceExecuteStrategy<DatasourceDTO> {

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private DatasourceDirTreeService datasourceDirTreeService;

    @Override
    public String mark() {
        return DataSourceTypeEnum.MYSQL.getValue();
    }

    @Override
    public CoreDatasource getCoreDatasource() {
        return null;
    }

    @Override
    @Transactional
    public Long addCoreData(DatasourceDTO datasourceDTO, User loginUser) {
        String configuration = datasourceDTO.getConfiguration();
        // 将JSON转换为对象
        StructDatabaseConfiguration structDatabaseConfiguration = JSONUtil.toBean(configuration, StructDatabaseConfiguration.class);
        // 重新进行一次校验
        MySQLUtil.checkConnectValid(structDatabaseConfiguration);
        // 根据pid存储到数据源文件树中
        AddDatasourceDirRequest addDatasourceDirRequest = AddDatasourceDirRequest
                .builder()
                .name(datasourceDTO.getName())
                .pid(datasourceDTO.getPid())
                .type(DirTypeEnum.FILE.getText())
                .build();
        datasourceDirTreeService.addDatasourceDirNode(addDatasourceDirRequest, loginUser);
        // 存储数据源信息
        CoreDatasource coreDatasource = new CoreDatasource();
        coreDatasource.setName(datasourceDTO.getName());
        coreDatasource.setDescription(datasourceDTO.getDescription());
        coreDatasource.setType(datasourceDTO.getType());
        coreDatasource.setConfiguration(configuration);
        coreDatasource.setUserId(loginUser.getId());
        ThrowUtils.throwIf(!coreDatasourceService.save(coreDatasource), ErrorCode.SYSTEM_ERROR);
        return coreDatasource.getId();
    }

    @Override
    public Boolean validDatasource(DatasourceDTO datasourceDTO) {
        String configuration = datasourceDTO.getConfiguration();
        // 将JSON转换为对象
        StructDatabaseConfiguration structDatabaseConfiguration = JSONUtil.toBean(configuration, StructDatabaseConfiguration.class);
        return MySQLUtil.checkConnectValid(structDatabaseConfiguration);
    }
}
