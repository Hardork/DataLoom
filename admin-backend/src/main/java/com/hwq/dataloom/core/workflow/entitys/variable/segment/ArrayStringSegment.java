package com.hwq.dataloom.core.workflow.entitys.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.List;

public class ArrayStringSegment extends ArraySegment {
    public ArrayStringSegment(List<String> value) {
        super(SegmentType.ARRAY_STRING, value);
    }
}