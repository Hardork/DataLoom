package com.hwq.dataloom.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author:HWQ
 * @DateTime:2023/10/11 23:19
 * @Description:
 **/
public enum OrderStatusEnum {
    SUCCESS("支付成功", "SUCCESS"),
    NOT_PAY("未支付", "NOT_PAY"),
    CANCEL("已取消", "CANCEL"),
    TIMEOUT("订单过期", "TIMEOUT");

    private final String text;

    private final String value;

    OrderStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static OrderStatusEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (OrderStatusEnum anEnum : OrderStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
