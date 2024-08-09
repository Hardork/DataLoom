package com.hwq.dataloom.utils.datasource;

import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MongoDBColumnValidator {

    public static final String SPECIAL_CHARACTERS = "~,.<>/;'\"`+-=|!@#$%^&*()[]{}";

    /**
     * 校验字段名是否符合 MongoDB 的存储格式要求
     * @param columnNames 需要校验的列名列表
     * @return 不符合要求的列名列表
     */
    public static boolean validateColumnNames(List<String> columnNames) {
        // 检查每个列名，找出不符合要求的列名
        for (String columnName : columnNames) {
            if (StringUtils.isEmpty(columnName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "首行行中不允许有空单元格！");
            }
            for (char c : SPECIAL_CHARACTERS.toCharArray()) {
                if (columnName.indexOf(c) >= 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
