package com.hwq.dataloom.core.workflow.entitys.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.List;

public class ArrayNumberSegment extends ArraySegment {
    public ArrayNumberSegment(List<? extends Number> value) {
        super(SegmentType.ARRAY_NUMBER, value);
    }
}