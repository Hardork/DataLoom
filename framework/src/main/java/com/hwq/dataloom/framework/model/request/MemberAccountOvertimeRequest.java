package com.hwq.dataloom.framework.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/16
 * @Description:
 **/
@Data
public class MemberAccountOvertimeRequest {

    private Long userId;
    private Integer time;
    private String overTimeUnit;

    @Getter
    @AllArgsConstructor
    public enum OverTimeUnit{
        DAY("DAY","天"),
        MONTH("MONTH","月"),
        YEAR("YEAR","年");

        private final String code;
        private final String desc;

    }
}
