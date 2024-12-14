package com.hwq.dataloom.core.workflow.node.end;

import com.hwq.dataloom.core.workflow.node.data.BaseNodeData;
import com.hwq.dataloom.core.workflow.variable.VariableSelector;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: HWQ
 * @Description: end节点数据
 * @DateTime: 2024/12/14 17:30
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class EndNodeData extends BaseNodeData {
    private List<VariableSelector> outputs;
}
