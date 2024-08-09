package com.hwq.bi.model.vo.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/6/17 23:24
 * @description
 */
@Data
public class QueryAICustomSQLVO {
    private List<String> columns;
    private List<Map<String, Object>> res;
    private String sql;
}
