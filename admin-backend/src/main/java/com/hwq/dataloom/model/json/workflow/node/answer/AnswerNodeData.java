package com.hwq.dataloom.model.json.workflow.node.answer;

import lombok.Builder;
import lombok.Data;

/**
 * @author HWQ
 * @date 2024/11/24 20:45
 * @description 节点响应结果类
 */
@Data
@Builder
public class AnswerNodeData {
    private String answer;
}
