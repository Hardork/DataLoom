package com.hwq.dataloom.core.workflow.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

/**
* 加密变量
 **/
public class SecretVariable extends StringVariable {


    public SecretVariable(Object value, Map<String, Object> mapping) {
        super(value, mapping);
        this.setValueType(SegmentType.SECRET);
    }
}
