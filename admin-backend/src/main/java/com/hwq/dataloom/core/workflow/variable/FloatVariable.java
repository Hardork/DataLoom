package com.hwq.dataloom.core.workflow.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

public class FloatVariable extends Variable {
    public FloatVariable(Object value, Map<String, Object> mapping) {
        super(SegmentType.NUMBER, value, mapping);
    }
}