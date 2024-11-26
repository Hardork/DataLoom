package com.hwq.dataloom.model.entity;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.*;

import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 工作流表
 * @TableName workflow
 */
@TableName(value ="workflow")
@Data
public class Workflow implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "workflowId", type = IdType.ASSIGN_ID)
    private Long workflowId;

    /**
     * 工作流名称
     */
    @TableField(value = "workflowName")
    private String workflowName;

    /**
     * 工作流图标
     */
    @TableField(value = "workflowIcon")
    private String workflowIcon;

    /**
     * 工作流作用描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 创建用户ID
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 工作流类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 版本信息
     */
    @TableField(value = "version")
    private String version;

    /**
     * 画布配置（JSON格式）
     */
    @TableField(value = "graph")
    private String graph;

    /**
     * 功能特性相关数据（JSON格式）
     */
    @TableField(value = "features")
    private String features;

    /**
     * 环境变量（JSON格式）
     */
    @TableField(value = "envVariables")
    private String envVariables;

    /**
     * 对话变量（JSON格式）
     */
    @TableField(value = "conversationVariables")
    private String conversationVariables;

    /**
     * 画布哈希值（用于判断是否变更）
     */
    @TableField(value = "uniqueHash")
    private String uniqueHash;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    /**
     * 获取graph对应的map
     * @return map
     */
    public Map<String, Object> graphDict() {
        if (StringUtils.isNotEmpty(graph)) {
            return JSONUtil.toBean(graph, new TypeReference<Map<String, Object>>() {}, false);
        }
        return new HashMap<>();
    }

    /**
     * 获取features对应的map
     * @return map
     */
    public Map<String, Object> featuresDict() {
        if (StringUtils.isNotEmpty(features)) {
            return JSONUtil.toBean(features, new TypeReference<Map<String, Object>>() {}, false);
        }
        return new HashMap<>();
    }

    /**
     * 获取画布的起始节点
     * @return 获取画布起始输入表单
     */
    public List<Map<String, Object>> findStartNodeInputForm() {
        if (StringUtils.isNotEmpty(graph)) {
            return new ArrayList<>();
        }
        Map<String, Object> graphDict = graphDict();
        try {
            List<Map<String, Object>> nodes = (List<Map<String, Object>>) graphDict.get("nodes");
            Optional<Map<String, Object>> startNodeOptional = nodes.stream()
                    .filter(node -> {
                        Map<String, Object> data = (Map<String, Object>) node.get("data");
                        return Objects.nonNull(data) && "start".equals(data.get("type"));
                    })
                    .findFirst();
            if (!startNodeOptional.isPresent()) {
                return Collections.emptyList();
            }
            Map<String, Object> startNode = startNodeOptional.get();
            Map<String, Object> data = (Map<String, Object>) startNode.get("data");
            return (List<Map<String, Object>>) data.get("variables");
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "画布解析失败");
        }
    }

    /**
     * 获取用户输入表单
     * @return 用户输入表单
     */
    public List<Map<String, Object>> userInputForm() {
        // 从图中获取起始节点，如果graph为空，返回空列表，这里需根据graph实际类型来判断是否为空，暂按此简单逻辑
        if (graph == null) {
            return new ArrayList<>();
        }

        // 查找类型为"start"的起始节点，使用Optional来处理可能不存在的情况
        List<Map<String, Object>> variables = findStartNodeInputForm();
        if (variables.isEmpty()) {
            return new ArrayList<>();
        }

        return variables;
    }

    /**
     * 根据graph和features计算画布的唯一哈希值
     * @return 画布唯一哈希值
     */
    public String uniqueHash() {
        Map<String, Object> entity = new HashMap<>();
        entity.put("graph", graph);
        entity.put("features", features);
        Map<String, Object> sortedEntity = new TreeMap<>(entity);
        String jsonStr = JSONUtil.toJsonStr(sortedEntity);
        return DigestUtils.md5Hex(jsonStr);
    }
}