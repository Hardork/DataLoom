package com.hwq.dataloom.core.workflow;

import com.hwq.dataloom.core.file.File;
import com.hwq.dataloom.core.workflow.config.FileExtraConfig;
import com.hwq.dataloom.core.workflow.enums.FileTransferMethod;
import com.hwq.dataloom.core.workflow.enums.FileType;
import com.hwq.dataloom.core.workflow.node.data.BaseNodeData;
import com.hwq.dataloom.core.workflow.node.data.LLMNodeData;
import com.hwq.dataloom.core.workflow.variable.VariablePool;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/12/10 00:01
 * @description 工作流运行实体
 */
public class WorkflowEntry {
    /**
     * 将用户输入映射到变量池
     * @param variableMapping
     * @param userInputs
     * @param variablePool
     * @param tenantId
     * @param nodeType
     * @param nodeData
     */
    public static void mappingUserInputsToVariablePool(
            Map<String, List<String>> variableMapping,
            Map<String, Object> userInputs,
            VariablePool variablePool,
            String tenantId,
            NodeTypeEnum nodeType,
            BaseNodeData nodeData) {
        // 遍历变量映射
        for (Map.Entry<String, List<String>> entry : variableMapping.entrySet()) {
            String nodeVariable = entry.getKey();
            List<String> variableSelector = entry.getValue();

            // 将节点变量拆分为节点 ID 和变量键
            String[] nodeVariableList = nodeVariable.split("\\.");
            if (nodeVariableList.length < 1) {
                throw new IllegalArgumentException("Invalid node variable " + nodeVariable);
            }

            String nodeVariableKey = String.join(".", Arrays.asList(nodeVariableList).subList(1, nodeVariableList.length));

            // 检查用户输入中是否存在变量键或节点变量，并且变量池中是否存在变量选择器
            if ((!userInputs.containsKey(nodeVariableKey) && !userInputs.containsKey(nodeVariable)) && variablePool.get(variableSelector) == null) {
                throw new IllegalArgumentException("Variable key " + nodeVariable + " not found in user inputs.");
            }

            // 从变量选择器中获取变量节点 ID
            String variableNodeId = variableSelector.get(0);
            List<String> variableKeyList = new ArrayList<>(variableSelector.subList(1, variableSelector.size()));

            // 从用户输入中获取输入值
            Object inputValue = userInputs.get(nodeVariable);
            if (inputValue == null) {
                inputValue = userInputs.get(nodeVariableKey);
            }

            // 处理 LLM 节点类型的特殊情况
            if (nodeType == NodeTypeEnum.LLM) {
                List<Object> newValue = new ArrayList<>();
                if (inputValue instanceof List) {
                    LLMNodeData llmNodeData = (LLMNodeData) nodeData;

//                    // TODO: 假设这里有获取配置详情的方法
//                    Object detail = llmNodeData.getVisionConfigs().getDetail();

                    for (Object item : (List<?>) inputValue) {
                        if (item instanceof Map) {
                            Map<String, Object> itemMap = (Map<String, Object>) item;
                            if (itemMap.containsKey("type") && "image".equals(itemMap.get("type"))) {
                                // TODO: 假设这里有将字符串转换为文件传输方法枚举的方法
                                FileTransferMethod transferMethod = FileTransferMethod.valueOf((String) itemMap.get("transfer_method"));
//                                File file = new File(
//                                        null,
//                                        FileType.IMAGE,
//                                        transferMethod,
//                                        transferMethod == FileTransferMethod.REMOTE_URL? (String) itemMap.get("url") : null,
//                                        transferMethod == FileTransferMethod.LOCAL_FILE? (String) itemMap.get("upload_file_id") : null,
//                                        new FileExtraConfig(
//                                        )
//                                );
//                                newValue.add(file);
                            }
                        }
                    }
                }

                if (!newValue.isEmpty()) {
                    inputValue = newValue;
                }
            }

            // 将变量和值添加到变量池
            List<String> list = Arrays.asList(variableNodeId);
            list.addAll(variableKeyList);
            variablePool.add(list, inputValue);
        }
    }
}
