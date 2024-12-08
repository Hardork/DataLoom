package com.hwq.dataloom.core.workflow.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

public class ArrayNumberVariable extends Variable {
    public ArrayNumberVariable(Object value, Map<String, Object> mapping) {
        super(SegmentType.ARRAY_NUMBER, value, mapping);
    }
}