package com.hwq.dataloom.core.workflow.entitys.variable.segment;

import com.hwq.dataloom.core.file.File;
import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayFileSegment extends ArraySegment {
    private List<File> value;

    public ArrayFileSegment(List<File> value) {
        super(SegmentType.ARRAY_FILE, value);
        this.value = value;
    }

    // 实现markdown属性对应的方法
    public String getMarkdown() {
        List<String> items = ((List<File>) this.value).stream().map(File::getFileName).collect(Collectors.toList());
        return String.join("\n", items);
    }
}