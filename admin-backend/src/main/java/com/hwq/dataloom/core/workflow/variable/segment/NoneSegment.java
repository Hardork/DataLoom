package com.hwq.dataloom.core.workflow.variable.segment;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

public class NoneSegment extends Segment {
    public NoneSegment() {
        super(SegmentType.NONE, null);
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public String getLog() {
        return "";
    }

    @Override
    public String getMarkdown() {
        return "";
    }
}