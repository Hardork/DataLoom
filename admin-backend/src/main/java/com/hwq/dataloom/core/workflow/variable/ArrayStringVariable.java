package com.hwq.dataloom.core.workflow.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

public class ArrayStringVariable extends Variable {
    public ArrayStringVariable(Object value, Map<String, Object> mapping) {
        super(SegmentType.ARRAY_STRING, value, mapping);
    }
}