package com.hwq.bi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.bi.model.dto.datasource.DataSourceConfig;
import com.hwq.bi.model.dto.datasource.PreviewData;
import com.hwq.bi.model.dto.datasource.PreviewDataRequest;
import com.hwq.bi.model.entity.DatasourceMetaInfo;
import com.hwq.bi.model.entity.User;

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
    PreviewData PreviewData(PreviewDataRequest previewDataRequest, DatasourceMetaInfo datasourceMetaInfo);

    /**
     * 存储数据源元数据
     * @param dataSourceConfig
     * @param loginUser
     * @return
     */
    Boolean saveDataSourceMetaInfo(DataSourceConfig dataSourceConfig, User loginUser);
}
