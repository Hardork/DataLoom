package com.hwq.dataloom.core.workflow.entitys.variable;

import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.io.File;
import java.util.Map;

public class FileVariable extends Variable {
    public FileVariable(Object value, Map<String, Object> mapping) {
        super(SegmentType.FILE, value, mapping);
    }
}