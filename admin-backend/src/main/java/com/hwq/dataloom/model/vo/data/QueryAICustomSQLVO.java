package com.hwq.dataloom.model.vo.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/6/17 23:24
 * @description 智能问数返回类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryAICustomSQLVO {
    private List<String> columns;
    private List<Map<String, Object>> res;
    private String sql;
}
