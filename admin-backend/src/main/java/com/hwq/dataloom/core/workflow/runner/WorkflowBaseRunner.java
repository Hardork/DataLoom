package com.hwq.dataloom.core.workflow.runner;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.entity.Workflow;
import com.hwq.dataloom.model.enums.workflow.RunConditionTypeEnum;
import com.hwq.dataloom.model.json.workflow.edge.Edge;
import com.hwq.dataloom.model.json.workflow.GraphParallel;
import com.hwq.dataloom.model.json.workflow.edge.GraphEdge;
import com.hwq.dataloom.model.json.workflow.edge.RunCondition;
import com.hwq.dataloom.model.json.workflow.node.Node;

import com.hwq.dataloom.model.json.workflow.Graph;
import org.apache.commons.lang3.StringUtils;

/**
 * @author HWQ
 * @date 2024/11/21 23:45
 * @description 工作流运行基类
 */
public class WorkflowBaseRunner {

    // TODO: 初始化Graph，包含寻找Start节点、
    public void run(Workflow workflow, String rootNodeId) {

        Graph graph = Graph.init(workflow.getGraph(), null);


        // init end stream param

    }




}
