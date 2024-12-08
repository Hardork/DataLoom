package com.hwq.dataloom.core.workflow.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

public class ArrayAnyVariable extends Variable {
    public ArrayAnyVariable(Object value, Map<String, Object> mapping) {
        super(SegmentType.ARRAY_ANY, value, mapping);
    }
}