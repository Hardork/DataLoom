package com.hwq.dataloom.core.workflow.entitys.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

public class NoneVariable extends Variable {
    public NoneVariable(Object value, Map<String, Object> mapping) {
        super(SegmentType.NONE, null, mapping);
    }
}