package com.hwq.dataloom.model.json.ai;

import lombok.Data;

import java.util.List;

/**
 * @Author: HWQ
 * @Description: 智能问数询问AI结果类
 * @DateTime: 2024/12/4 9:12
 **/
@Data
public class UserChatForSQLRes {
    /**
     * 此次查询关联的表
     */
    private List<String> relateTables;

    /**
     * 此次查询统计记录行数的sql
     */
    private String countSql;

    /**
     * 查询sql
     */
    private String sql;
}
