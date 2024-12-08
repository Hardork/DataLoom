package com.hwq.dataloom.core.workflow.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

public class ObjectVariable extends Variable {
    public ObjectVariable(Object value, Map<String, Object> mapping) {
        super(SegmentType.OBJECT, value, mapping);
    }
}