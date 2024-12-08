package com.hwq.dataloom.core.workflow.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

public class ArrayObjectVariable extends Variable {
    public ArrayObjectVariable(Object value, Map<String, Object> mapping) {
        super(SegmentType.ARRAY_OBJECT, value, mapping);
    }
}