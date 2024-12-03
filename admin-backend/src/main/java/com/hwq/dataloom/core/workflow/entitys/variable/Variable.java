package com.hwq.dataloom.core.workflow.entitys.variable;

import com.hwq.dataloom.core.workflow.entitys.variable.segment.Segment;
import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

/**
 * 变量类
 */
public abstract class Variable extends Segment {
    private String id;
    private String name;
    private String description;

    public Variable(SegmentType valueType, Object value, Map<String, Object> mapping) {
        super(valueType, value);
        this.id = (String) mapping.get("id");
        this.name = (String) name;
        this.description = (String) description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}

