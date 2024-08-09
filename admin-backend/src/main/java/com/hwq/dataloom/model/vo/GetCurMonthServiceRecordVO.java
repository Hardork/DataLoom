package com.hwq.dataloom.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author:HWQ
 * @DateTime:2023/9/29 16:16
 * @Description:
 **/
@Data
public class GetCurMonthServiceRecordVO {
    // 服务调用类型
    private String serviceType;
    // 服务调用次数统计
    private List<Long> serviceData;
    // 服务调用日期
    private List<String> serviceDate;
}
