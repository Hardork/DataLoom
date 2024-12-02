package com.hwq.dataloom.core.workflow.entitys.variable.segment;

import com.hwq.dataloom.core.file.File;
import com.hwq.dataloom.core.workflow.enums.SegmentType;

// 实现FileSegment类
public class FileSegment extends Segment {
    // 按照Python代码设定默认的value_type为FILE类型
    private static final SegmentType VALUE_TYPE = SegmentType.FILE;
    private File value;

    public FileSegment(File value) {
        super(VALUE_TYPE, value);
        this.value = value;
    }

    // 实现markdown属性对应的方法，获取文件对象的markdown内容
    public String getMarkdown() {
        return value.getMarkdown();
    }

    // 实现log属性对应的方法，将文件对象转换为字符串返回（这里简单调用toString方法示例，实际可能按业务需求格式化）
    public String getLog() {
        return value.toString();
    }

    // 实现text属性对应的方法，同样将文件对象转换为字符串返回
    public String getText() {
        return value.toString();
    }
}