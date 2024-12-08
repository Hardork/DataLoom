package com.hwq.dataloom.core.workflow.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.List;
import java.util.Map;

public class ArrayObjectSegment extends ArraySegment {
    public ArrayObjectSegment(List<Map<String, Object>> value) {
        super(SegmentType.ARRAY_OBJECT, value);
    }
}