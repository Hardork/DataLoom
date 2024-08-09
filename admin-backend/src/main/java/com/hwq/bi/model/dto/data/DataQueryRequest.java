package com.hwq.bi.model.dto.data;

import com.hwq.bi.common.PageRequest;
import com.hwq.bi.mongo.entity.ChartData;
import lombok.Data;
/**
 * @author HWQ
 * @date 2024/4/25 19:56
 * @description
 */
@Data
public class DataQueryRequest extends PageRequest {
    private Long dataId;
    private ChartData chartData;
}