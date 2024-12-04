package com.hwq.dataloom.websocket.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/6/18 20:25
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AskSQLWebSocketMsgVO {
    private List<String> columns;
    private List<Map<String, Object>> res;
    private String sql;
    private String type;
    private Long total;
}
