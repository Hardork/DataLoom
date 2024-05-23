package com.hwq.bi.model.dto.datasource;

import lombok.Data;

/**
 * @author HWQ
 * @date 2024/5/24 01:39
 * @description
 */
@Data
public class PreviewDataRequest extends DataSourceConfig{
    private String dataName;
}
