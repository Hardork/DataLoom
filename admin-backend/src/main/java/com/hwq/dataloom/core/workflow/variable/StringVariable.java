package com.hwq.dataloom.core.workflow.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

public class StringVariable extends Variable {
    public StringVariable(Object value, Map<String, Object> mapping) {
        super(SegmentType.STRING, value, mapping);
    }
}