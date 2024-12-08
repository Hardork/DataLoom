package com.hwq.dataloom.core.workflow.variable.segment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwq.dataloom.core.workflow.enums.SegmentType;

import java.util.Map;

public class ObjectSegment extends Segment {
    public ObjectSegment(Map<String, Object> value) {
        super(SegmentType.OBJECT, value);
    }

    @Override
    public String getText() {
        try {
            return new ObjectMapper().writeValueAsString(this.getValue());
        } catch (JsonProcessingException e) {
            // 这里可以根据实际情况更好地处理异常，目前简单返回空字符串示例
            return "";
        }
    }

    @Override
    public String getLog() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this.getValue());
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    @Override
    public String getMarkdown() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this.getValue());
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}