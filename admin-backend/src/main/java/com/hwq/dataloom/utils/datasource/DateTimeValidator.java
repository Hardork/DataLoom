package com.hwq.dataloom.utils.datasource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class DateTimeValidator {

    // 定义日期时间模式列表
    private static final List<String> PATTERNS = Arrays.asList(
            "yyyy-MM-dd",               // 年-月-日
            "HH:mm:ss",                 // 时:分:秒
            "yyyy-MM-dd HH:mm:ss",      // 年-月-日 时:分:秒
            "yyyy-MM-dd'T'HH:mm:ss"     // ISO 8601日期时间格式
    );

    public static boolean isDateTime(String input) {
        // 遍历每个模式
        for (String pattern : PATTERNS) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            try {
                // 根据模式包含的内容选择解析方法
                if (pattern.contains("HH")) {
                    // 如果模式包含时间元素，尝试解析为 LocalDateTime 或 LocalTime
                    if (pattern.contains("yyyy-MM-dd")) {
                        LocalDateTime.parse(input, formatter);
                    } else {
                        LocalTime.parse(input, formatter);
                    }
                } else {
                    // 否则，解析为 LocalDate
                    LocalDate.parse(input, formatter);
                }
                return true; // 如果解析成功，返回 true
            } catch (DateTimeParseException e) {
                // 如果解析失败，捕获异常并继续尝试下一个模式
            }
        }
        return false; // 如果所有模式都不能解析输入字符串，则返回 false
    }
}
