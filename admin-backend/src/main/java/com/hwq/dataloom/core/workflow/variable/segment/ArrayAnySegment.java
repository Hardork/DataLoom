package com.hwq.dataloom.core.workflow.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.List;

public class ArrayAnySegment extends ArraySegment {
    public ArrayAnySegment(List<?> value) {
        super(SegmentType.ARRAY_ANY, value);
    }
}