package com.hwq.dataloom.core.workflow.enums;

/**
 * 文件传输方式
 */
public enum FileTransferMethod {
    REMOTE_URL("remote_url"),
    LOCAL_FILE("local_file"),
    TOOL_FILE("tool_file");

    private final String value;

    FileTransferMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 实现类似Python中value_of方法的功能，根据传入的值查找对应的枚举成员
    public static FileTransferMethod fromValue(String value) {
        for (FileTransferMethod member : FileTransferMethod.values()) {
            if (member.getValue().equals(value)) {
                return member;
            }
        }
        throw new IllegalArgumentException("No matching enum found for value: " + value);
    }
}