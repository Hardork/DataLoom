package com.hwq.dataloom.core.workflow.entitys.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.List;
import java.util.Map;

public class ArrayNumberVariable extends Variable {
    public ArrayNumberVariable(Object value, Map<String, Object> mapping) {
        super(SegmentType.ARRAY_NUMBER, value, mapping);
    }
}