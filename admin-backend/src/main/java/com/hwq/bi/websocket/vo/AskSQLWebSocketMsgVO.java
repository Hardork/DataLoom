package com.hwq.bi.websocket.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/6/18 20:25
 * @description
 */
@Data
public class AskSQLWebSocketMsgVO {
    private List<String> columns;
    private List<Map<String, Object>> res;
    private String sql;
    private String type;
}
