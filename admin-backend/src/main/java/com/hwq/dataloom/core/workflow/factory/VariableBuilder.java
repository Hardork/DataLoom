package com.hwq.dataloom.core.workflow.factory;

import com.hwq.dataloom.core.file.File;
import com.hwq.dataloom.core.workflow.enums.SegmentType;
import com.hwq.dataloom.core.workflow.variable.*;
import com.hwq.dataloom.core.workflow.variable.segment.*;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;

import java.util.*;

/**
 * 变量构造器
 */
public class VariableBuilder {

    // MAX_VARIABLE_SIZE的常量（按实际配置获取其值）
    private static final int MAX_VARIABLE_SIZE = 1024;

    public static Variable buildVariableFromMapping(Map<String, Object> mapping) throws BusinessException {
        if (!mapping.containsKey("value_type")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "missing value type");
        }
        if (!mapping.containsKey("name")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"missing name");
        }
        if (!mapping.containsKey("value")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"missing value");
        }

        SegmentType valueType = SegmentType.valueOf((String) mapping.get("value_type"));
        Object value = mapping.get("value");

        Variable result;
        switch (valueType) {
            case STRING:
                result = new StringVariable(value, mapping);
                break;
            case NUMBER:
                if (value instanceof Integer) {
                    result = new IntegerVariable(value, mapping);
                } else if (value instanceof Float) {
                    result = new FloatVariable(value, mapping);
                } else {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,"invalid number value " + value);
                }
                break;
            case OBJECT:
                if (value instanceof Map) {
                    result = new ObjectVariable(value, mapping);
                } else {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,"invalid object value " + value);
                }
                break;
            case ARRAY_STRING:
                if (value instanceof List) {
                    result = new ArrayStringVariable(value, mapping);
                } else {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,"invalid array string value " + value);
                }
                break;
            case ARRAY_NUMBER:
                if (value instanceof List) {
                    result = new ArrayNumberVariable(value, mapping);
                } else {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,"invalid array number value " + value);
                }
                break;
            case ARRAY_OBJECT:
                if (value instanceof List) {
                    result = new ArrayObjectVariable(value, mapping);
                } else {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,"invalid array object value " + value);
                }
                break;
            default:
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"not supported value type " + valueType);
        }

        if (result.getSize() > MAX_VARIABLE_SIZE) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"variable size " + result.getSize() + " exceeds limit " + MAX_VARIABLE_SIZE);
        }

        return result;
    }

    public static Segment buildSegment(Object value){
        if (value == null) {
            return new NoneSegment();
        } else if (value instanceof String) {
            return new StringSegment((String) value);
        } else if (value instanceof Integer) {
            return new IntegerSegment((Integer) value);
        } else if (value instanceof Float) {
            return new FloatSegment((Float) value);
        } else if (value instanceof java.util.Map) {
            return new ObjectSegment((java.util.Map<String, Object>) value);
        } else if (value instanceof File) {
            return new FileSegment((File) value);
        } else if (value instanceof List) {
            List<Segment> items = new ArrayList<>();
            for (Object item : (List<?>) value) {
                items.add(buildSegment(item));
            }
            Set<SegmentType> types = new HashSet<>();
            for (Segment segment : items) {
                types.add(segment.getValueType());
            }
            if (types.size()!= 1 || items.stream().allMatch(s -> s instanceof ArraySegment)) {
                return new ArrayAnySegment((List<?>) value);
            }
            SegmentType type = types.iterator().next();
            switch (type) {
                case STRING:
                    return new ArrayStringSegment((List<String>) value);
                case NUMBER:
                    return new ArrayNumberSegment((List<? extends Number>) value);
                case OBJECT:
                    return new ArrayObjectSegment((List<java.util.Map<String, Object>>) value);
                case FILE:
                    return new ArrayFileSegment((List<File>) value);
                default:
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "not supported value " + value);
            }
        } else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "not supported value " + value);
        }
    }
}