package com.hwq.dataloom.core.workflow.node.answer.route_chunk;

import com.hwq.dataloom.core.workflow.enums.ChunkType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: HWQ
 * @Description: 变量生成块
 * @DateTime: 2024/12/12 17:03
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class VarGenerateRouteChunk extends GenerateRouteChunk {

    private List<String> valueSelector;

    public VarGenerateRouteChunk(List<String> valueSelector) {
        super(ChunkType.VAR);
        this.valueSelector = valueSelector;
    }
}
