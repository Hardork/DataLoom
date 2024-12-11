package com.hwq.dataloom.core.workflow.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据片段类
 */
@Data
public abstract class Segment implements Serializable {

    /**
     * 值类型
     */
    private SegmentType valueType;

    /**
     * 值
     */
    private Object value;

    public Segment() {}

    public Segment(SegmentType valueType, Object value) {
        this.valueType = valueType;
        this.value = value;
    }

    /**
     * 返回值的字符串
     * @return 值的字符串
     */
    public String getText() {
        return String.valueOf(this.value);
    }

    /**
     * 获取日志内容
     * @return 日志内容
     */
    public String getLog() {
        return String.valueOf(this.value);
    }

    /**
     * 获取markdown格式内容
     * @return markdown格式内容
     */
    public String getMarkdown() {
        return String.valueOf(this.value);
    }

    /**
     * 获取当前值的大小
     * @return 值大小
     */
    public int getSize() {
        // 这里简单返回一个固定值示例，实际要根据对象真实大小计算，可能较复杂，比如序列化后看字节长度等
        return 0;
    }

    public Object toObject() {
        return this.value;
    }
}