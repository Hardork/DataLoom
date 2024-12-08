package com.hwq.dataloom.core.workflow.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

public class IntegerSegment extends Segment {
    public IntegerSegment(int value) {
        super(SegmentType.NUMBER, value);
    }
}