package com.hwq.dataloom.service.impl;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.mapper.DatasourceMetaInfoMapper;
import com.hwq.dataloom.model.dto.datasource.DataSourceConfig;
import com.hwq.dataloom.model.dto.datasource.PreviewData;
import com.hwq.dataloom.model.dto.datasource.PreviewDataRequest;
import com.hwq.dataloom.model.entity.DatasourceMetaInfo;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.service.DatasourceMetaInfoService;
import com.hwq.dataloom.utils.datasource.MySQLUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author wqh
* @description 针对表【datasource_meta_info】的数据库操作Service实现
* @createDate 2024-05-24 11:32:29
*/
@Service
public class DatasourceMetaInfoServiceImpl extends ServiceImpl<DatasourceMetaInfoMapper, DatasourceMetaInfo>
    implements DatasourceMetaInfoService{

    @Override
    public PreviewData PreviewData(PreviewDataRequest previewDataRequest, DatasourceMetaInfo datasourceMetaInfo) {
        // check
        String dataName = previewDataRequest.getDataName();
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        BeanUtils.copyProperties(datasourceMetaInfo, dataSourceConfig);
        dataSourceConfig.setType("");
        return MySQLUtil.getPreviewData(dataSourceConfig, dataName);
    }

    @Override
    public Boolean saveDataSourceMetaInfo(DataSourceConfig dataSourceConfig, User loginUser) {
        ThrowUtils.throwIf(dataSourceConfig == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Long userId = loginUser.getId();
        DatasourceMetaInfo datasourceMetaInfo = new DatasourceMetaInfo();
        BeanUtils.copyProperties(dataSourceConfig, datasourceMetaInfo);
        datasourceMetaInfo.setUserId(userId);
        boolean save = this.save(datasourceMetaInfo);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        return true;
    }

    @Override
    public List<String> getSchemas(DatasourceMetaInfo datasourceMetaInfo) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        BeanUtils.copyProperties(datasourceMetaInfo, dataSourceConfig);
        return MySQLUtil.getSchemas(dataSourceConfig);
    }
}



