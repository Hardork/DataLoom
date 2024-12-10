package com.hwq.dataloom.core.workflow.variable;

import com.hwq.dataloom.core.workflow.variable.segment.Segment;
import com.hwq.dataloom.core.workflow.enums.SegmentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

/**
 * 变量类
 */
@EqualsAndHashCode(callSuper = true)
@Data
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

}

