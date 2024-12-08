package com.hwq.dataloom.core.workflow.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.io.Serializable;

public abstract class Segment implements Serializable {
    private SegmentType valueType;
    private Object value;

    public Segment(SegmentType valueType, Object value) {
        this.valueType = valueType;
        this.value = value;
    }

    public SegmentType getValueType() {
        return valueType;
    }

    public Object getValue() {
        return value;
    }

    public String getText() {
        return String.valueOf(this.value);
    }

    public String getLog() {
        return String.valueOf(this.value);
    }

    public String getMarkdown() {
        return String.valueOf(this.value);
    }

    public int getSize() {
        // 这里简单返回一个固定值示例，实际要根据对象真实大小计算，可能较复杂，比如序列化后看字节长度等
        return 0;
    }

    public Object toObject() {
        return this.value;
    }
}