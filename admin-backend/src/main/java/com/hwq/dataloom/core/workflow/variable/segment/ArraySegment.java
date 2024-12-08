package com.hwq.dataloom.core.workflow.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ArraySegment extends Segment {
    public ArraySegment(SegmentType valueType, Object value) {
        super(valueType, value);
    }

    @Override
    public String getMarkdown() {
        if (this.getValue() instanceof List<?>) {
            List<?> items = (List<?>) this.getValue();
            List<String> itemStringList = items.stream().map(Object::toString).collect(Collectors.toList());
            return String.join("\n", itemStringList);
        }
        return "";
    }
}