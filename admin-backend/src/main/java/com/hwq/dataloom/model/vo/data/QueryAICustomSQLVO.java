package com.hwq.dataloom.model.vo.data;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/6/17 23:24
 * @description 智能问数返回类
 */
@Data
public class QueryAICustomSQLVO {
    private List<String> columns;
    private List<Map<String, Object>> res;
    private String sql;
}
