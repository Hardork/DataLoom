package com.hwq.dataloom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.model.json.datasource.StructDatabaseConfiguration;
import com.hwq.dataloom.model.dto.datasource.PreviewData;
import com.hwq.dataloom.model.dto.datasource.PreviewDataRequest;
import com.hwq.dataloom.model.entity.DatasourceMetaInfo;
import com.hwq.dataloom.framework.model.entity.User;

import java.sql.SQLException;
import java.util.List;

/**
* @author wqh
* @description 针对表【datasource_meta_info】的数据库操作Service
* @createDate 2024-05-24 11:32:29
*/
public interface DatasourceMetaInfoService extends IService<DatasourceMetaInfo> {


    /**
     * 查看表结构
     * @param previewDataRequest
     * @param datasourceMetaInfo
     * @return
     */
    PreviewData PreviewData(PreviewDataRequest previewDataRequest, DatasourceMetaInfo datasourceMetaInfo) throws SQLException;

    /**
     * 存储数据源元数据
     * @param structDatabaseConfiguration
     * @param loginUser
     * @return
     */
    Boolean saveDataSourceMetaInfo(StructDatabaseConfiguration structDatabaseConfiguration, User loginUser);

    /**
     * 获取表结构
     * @param datasourceMetaInfo
     * @return
     */
    List<String> getSchemas(DatasourceMetaInfo datasourceMetaInfo);
}
