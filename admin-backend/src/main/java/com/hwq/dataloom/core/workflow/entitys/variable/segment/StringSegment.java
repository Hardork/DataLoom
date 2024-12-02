package com.hwq.dataloom.core.workflow.entitys.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

public class StringSegment extends Segment {
    public StringSegment(String value) {
        super(SegmentType.STRING, value);
    }
}