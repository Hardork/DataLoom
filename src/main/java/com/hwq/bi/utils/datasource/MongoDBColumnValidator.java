package com.hwq.bi.utils.datasource;

import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoDBColumnValidator {

    /**
     * 校验字段名是否符合 MongoDB 的存储格式要求
     * @param columnNames 需要校验的列名列表
     * @return 不符合要求的列名列表
     */
    public static void validateColumnNames(List<String> columnNames) {
        // 定义特殊字符
        String invalidCharDot = ".";
        String invalidCharDollar = "$";
        // 检查每个列名，找出不符合要求的列名
        columnNames.forEach(columnName -> {
            if (StringUtils.isEmpty(columnName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "首行行中不允许有空单元格！");
            }
            if (columnName.contains(invalidCharDot) || columnName.startsWith(invalidCharDollar)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "表头中不应包含. $ 等特殊字符");
            }
        });
    }
}
