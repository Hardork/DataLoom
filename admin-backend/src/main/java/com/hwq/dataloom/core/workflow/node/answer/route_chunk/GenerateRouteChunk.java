package com.hwq.dataloom.core.workflow.node.answer.route_chunk;

import com.hwq.dataloom.core.workflow.enums.ChunkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HWQ
 * @date 2024/11/24 20:31
 * @description 响应块
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRouteChunk {
    private ChunkType type;
}

