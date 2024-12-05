package com.hwq.dataloom.websocket.vo;

import com.hwq.dataloom.utils.datasource.CustomPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/6/18 20:25
 * @description 智能问数消息返回类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AskSQLWebSocketMsgVO {
    private CustomPage<Map<String, Object>> data;
    private Integer type;
    private String message;
}
