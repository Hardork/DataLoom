package com.hwq.dataloom.utils;

import com.google.common.collect.ImmutableList;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.calcite.sql.SqlKind.*;

/**
 * SQL 工具
 *
 * @author <a href="https://github.com/Hardork">老山羊</a>
 * 
 */
public class SqlUtils {

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }

}
