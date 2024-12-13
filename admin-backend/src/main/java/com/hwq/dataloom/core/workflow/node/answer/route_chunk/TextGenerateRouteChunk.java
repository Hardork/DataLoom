package com.hwq.dataloom.core.workflow.node.answer.route_chunk;

import com.hwq.dataloom.core.workflow.enums.ChunkType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: HWQ
 * @Description: 文本生成块
 * @DateTime: 2024/12/12 17:14
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TextGenerateRouteChunk extends GenerateRouteChunk {
    private String text;
    public TextGenerateRouteChunk(String text) {
        super(ChunkType.TEXT);
        this.text = text;
    }
}
