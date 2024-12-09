package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: 运行时路由状态
 * @DateTime: 2024/12/9 13:48
 **/
@Data
public class RuntimeRouteState {
    /**
     * 路由
     */
    private Map<String, List<String>> routes;

    /**
     * 节点状态映射
     */
    private Map<String, RouteNodeState> nodeStateMapping;

    /**
     * 根据给定的节点 ID 创建新的节点状态，并添加到映射中
     *
     * @param nodeId 节点 ID
     * @return 创建的 RouteNodeState 对象
     */
    public RouteNodeState createNodeState(String nodeId) {
        // 创建新的 RouteNodeState 对象，并设置当前时间为开始时间
        RouteNodeState state = new RouteNodeState(nodeId, ZonedDateTime.now(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        // 将新创建的节点状态添加到映射中
        nodeStateMapping.put(state.getId(), state);
        return state;
    }

    /**
     * 向路由中添加从源节点状态到目标节点状态的连接
     *
     * @param sourceNodeStateId 源节点状态 ID
     * @param targetNodeStateId 目标节点状态 ID
     */
    public void addRoute(String sourceNodeStateId, String targetNodeStateId) {
        // 如果源节点状态 ID 尚未在路由中，创建一个新的列表
        if (!routes.containsKey(sourceNodeStateId)) {
            routes.put(sourceNodeStateId, new ArrayList<>());
        }
        // 将目标节点状态 ID 添加到源节点状态的目标列表中
        routes.get(sourceNodeStateId).add(targetNodeStateId);
    }

    /**
     * 根据给定的源节点状态 ID 获取相关的目标节点状态列表
     *
     * @param sourceNodeStateId 源节点状态 ID
     * @return 包含目标节点状态的列表
     */
    public List<RouteNodeState> getRoutesWithNodeStateBySourceNodeStateId(String sourceNodeStateId) {
        // 创建结果列表
        List<RouteNodeState> result = new ArrayList<>();
        // 获取源节点状态 ID 对应的目标节点状态 ID 列表
        List<String> targetStateIds = routes.get(sourceNodeStateId);
        // 如果目标节点状态 ID 列表不为空
        if (targetStateIds!= null) {
            // 遍历目标节点状态 ID 列表
            for (String targetStateId : targetStateIds) {
                // 根据目标节点状态 ID 从映射中获取节点状态对象，并添加到结果列表中（如果存在）
                RouteNodeState state = nodeStateMapping.get(targetStateId);
                if (state!= null) {
                    result.add(state);
                }
            }
        }
        // 返回结果列表
        return result;
    }
}
