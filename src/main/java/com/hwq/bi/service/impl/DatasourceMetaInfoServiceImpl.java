package com.hwq.bi.service.impl;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.mapper.DatasourceMetaInfoMapper;
import com.hwq.bi.model.dto.datasource.DataSourceConfig;
import com.hwq.bi.model.dto.datasource.PreviewData;
import com.hwq.bi.model.dto.datasource.PreviewDataRequest;
import com.hwq.bi.model.dto.datasource.SchemaStructure;
import com.hwq.bi.model.entity.DatasourceMetaInfo;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.service.DatasourceMetaInfoService;
import com.hwq.bi.utils.datasource.AESUtils;
import com.hwq.bi.utils.datasource.DruidUtil;
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
        return DruidUtil.getPreviewData(dataSourceConfig, dataName);
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
        return DruidUtil.getSchemas(dataSourceConfig);
    }
}




