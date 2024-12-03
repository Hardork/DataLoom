package com.hwq.dataloom.core.workflow.entitys.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

public class FloatSegment extends Segment {
    public FloatSegment(float value) {
        super(SegmentType.NUMBER, value);
    }
}