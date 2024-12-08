package com.hwq.dataloom.core.workflow.node.answer;

import com.hwq.dataloom.core.workflow.node.data.BaseNodeData;
import lombok.Builder;
import lombok.Data;

/**
 * @author HWQ
 * @date 2024/11/24 20:45
 * @description 节点响应结果类
 */
@Data
@Builder
public class AnswerNodeData extends BaseNodeData {
    private String answer;
}
