package com.hwq.bi.mongo.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Map;

/**
 * @author HWQ
 * @date 2024/4/19 17:03
 * @description
 */
@Data
@Accessors(chain = true)
public class ChartData{
    @MongoId(FieldType.STRING)
    private String id;
    private Map<String, Object> data;
}
