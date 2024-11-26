package com.hwq.dataloom.core.ops.entitys;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: 跟踪信息基类
 * @DateTime: 2024/11/26 11:02
 **/
@Data
public class BaseTraceInfo {
    private String messageId;
    private Object messageData;
    private Object inputs;
    private Object outputs;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Map<String, Object> metaData;
}
