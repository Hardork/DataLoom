package com.hwq.dataloom.core.file;

/**
 * 文件属性
 */
public enum FileAttribute {
    TYPE("type"),
    SIZE("size"),
    NAME("name"),
    MIME_TYPE("mime_type"),
    TRANSFER_METHOD("transfer_method"),
    URL("url"),
    EXTENSION("extension");

    private final String value;

    FileAttribute(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}