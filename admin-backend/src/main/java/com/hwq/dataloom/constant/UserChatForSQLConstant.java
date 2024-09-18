package com.hwq.dataloom.constant;

/**
 * @author HWQ
 * @date 2024/9/7 18:12
 * @description 智能问数常量类
 */
public interface UserChatForSQLConstant {
    String ANALYSIS_QUESTION = "分析需求:{%s}";

    String TABLES_AND_FIELDS_PART = "所有的表与字段信息:[%s]";

    String TABLE_INFO = "{表名:%s, 表注释:%s, 字段列表:[%s]}";

    String FIELDS_INFO = "{字段名:%s, 字段注释:%s, 字段类型:%s}";

    String LIST_INFO = "[%s]";

    String SPLIT = ",";

    Integer LIMIT_RECORDS = 20;
}
