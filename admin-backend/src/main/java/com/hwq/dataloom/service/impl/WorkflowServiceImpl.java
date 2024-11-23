package com.hwq.dataloom.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.workflow.AddWorkflowDTO;
import com.hwq.dataloom.model.dto.workflow.QueryWorkflowDTO;
import com.hwq.dataloom.model.dto.workflow.SaveWorkflowDTO;
import com.hwq.dataloom.model.dto.workflow.UpdateWorkflowDTO;
import com.hwq.dataloom.model.entity.Workflow;
import com.hwq.dataloom.model.enums.workflow.WorkflowTypeEnum;
import com.hwq.dataloom.model.enums.workflow.WorkflowVersionEnum;
import com.hwq.dataloom.model.json.workflow.Graph;
import com.hwq.dataloom.model.vo.workflow.GetWorkflowDaftVO;
import com.hwq.dataloom.model.vo.workflow.SaveWorkflowDraftVO;
import com.hwq.dataloom.model.vo.workflow.WorkflowVO;
import com.hwq.dataloom.service.WorkflowService;
import com.hwq.dataloom.mapper.WorkflowMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author HWQ
* @description 针对表【workflow(工作流表)】的数据库操作Service实现
* @createDate 2024-11-18 13:41:53
*/
@Service
@Slf4j
public class WorkflowServiceImpl extends ServiceImpl<WorkflowMapper, Workflow>
    implements WorkflowService{


    @Value("workflow.defaultIcon")
    private String defaultIcon;


    @Override
    public SaveWorkflowDraftVO syncWorkflowDraft(SaveWorkflowDTO saveWorkflowDTO, User loginUser) {
        // valid params
        Graph graph = saveWorkflowDTO.getGraph();
        List<String> envVariables = saveWorkflowDTO.getEnvVariables();
        List<String> conversationVariables = saveWorkflowDTO.getConversationVariables();
        Map<String, Object> features = saveWorkflowDTO.getFeatures();
        ThrowUtils.throwIf(graph == null, ErrorCode.PARAMS_ERROR, "graph参数错误");
        String hashUnique = saveWorkflowDTO.getHashUnique();
        ThrowUtils.throwIf(StringUtils.isEmpty(hashUnique), ErrorCode.PARAMS_ERROR, "uniqueHash不得为空");
        Long workflowId = saveWorkflowDTO.getWorkflowId();
        ThrowUtils.throwIf(workflowId == null, ErrorCode.PARAMS_ERROR, "id不得为空");
        Workflow workflow = this.getById(workflowId);
        ThrowUtils.throwIf(workflow == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!workflow.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限");
        ThrowUtils.throwIf(!workflow.getUniqueHash().equals(hashUnique), ErrorCode.OPERATION_ERROR, "更新失败，请先同步画布");
        // fill workflow field
        workflow.setGraph(JSONUtil.toJsonStr(graph));
        workflow.setFeatures(JSONUtil.toJsonStr(features));
        workflow.setEnvVariables(JSONUtil.toJsonStr(envVariables));
        workflow.setConversationVariables(JSONUtil.toJsonStr(conversationVariables));
        // get workflow new uniqueHash
        String newUniqueHash = workflow.uniqueHash();
        // update workflow
        workflow.setUniqueHash(newUniqueHash);
        boolean update = this.updateById(workflow);
        if (!update) {
            log.error("更新工作流失败，工作流:{}", workflow);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return SaveWorkflowDraftVO.builder()
                .uniqueHash(newUniqueHash)
                .status("success").build();
    }

    @Override
    public GetWorkflowDaftVO getWorkflowDraft(Long workflowId, User loginUser) {
        // valid params
        Workflow workflow = this.getById(workflowId);
        ThrowUtils.throwIf(workflow == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!workflow.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        // build workflowDraftVO
        return GetWorkflowDaftVO.builder()
                .graph(JSONUtil.toBean(workflow.getGraph(), Graph.class))
                .envVariables(JSONUtil.toList(workflow.getEnvVariables(), String.class))
                .features(JSONUtil.toBean(workflow.getFeatures(), Map.class))
                .conversationVariables(JSONUtil.toList(workflow.getConversationVariables(), String.class))
                .uniqueHash(workflow.getUniqueHash())
                .build();
    }

    @Override
    public void runWorkflowDraft(Long workflowId, User loginUser) {
        // valid params
        Workflow workflow = this.getById(workflowId);
        ThrowUtils.throwIf(workflow == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(!workflow.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        // init graph config

    }

    @Override
    public Page<WorkflowVO> selectPage(QueryWorkflowDTO queryWorkflowDTO, User loginUser) {
        LambdaQueryWrapper<Workflow> lqw = new LambdaQueryWrapper<>();
        String workflowName = queryWorkflowDTO.getWorkflowName();
        if (!StringUtils.isEmpty(workflowName)) {
            lqw.likeLeft(Workflow::getWorkflowName, workflowName);
        }
        Page<Workflow> page = this.page(new Page<>(queryWorkflowDTO.getCurrent(), queryWorkflowDTO.getPageSize()), lqw);
        return convertPageEntity2VO(page);
    }

    @Override
    public Workflow addWorkflow(AddWorkflowDTO addWorkflowRequest, User loginUser) {
        String workflowName = addWorkflowRequest.getWorkflowName();
        String workflowIcon = addWorkflowRequest.getWorkflowIcon();
        String type = addWorkflowRequest.getType();
        ThrowUtils.throwIf(StringUtils.isEmpty(workflowName), ErrorCode.PARAMS_ERROR, "工作流名称不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(type), ErrorCode.PARAMS_ERROR, "工作流类型不得为空");
        WorkflowTypeEnum workflowTypeEnum = WorkflowTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(workflowTypeEnum == null, ErrorCode.PARAMS_ERROR, "不存在对应类型的工作流");
        Workflow workflow = new Workflow();
        BeanUtils.copyProperties(addWorkflowRequest, workflow);
        workflow.setUserId(loginUser.getId());
        // 初始化工作流的唯一哈希
        workflow.setUniqueHash(workflow.uniqueHash());
        // 初始化工作流的版本信息
        workflow.setVersion(WorkflowVersionEnum.DRAFT.getValue());
        if (StringUtils.isEmpty(workflowIcon)) {
            workflow.setWorkflowIcon(defaultIcon);
        }
        boolean save = this.save(workflow);
        if (!save) {
            log.error("保存工作流失败，工作流：{}", workflow);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return workflow;
    }

    @Override
    public Boolean updateWorkflow(UpdateWorkflowDTO updateWorkflowDTO, User loginUser) {
        Long workflowId = updateWorkflowDTO.getWorkflowId();
        String workflowName = updateWorkflowDTO.getWorkflowName();
        ThrowUtils.throwIf(workflowId == null, ErrorCode.PARAMS_ERROR, "id不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(workflowName), ErrorCode.PARAMS_ERROR, "工作流名称不得为空");
        Workflow workflow = this.getById(workflowId);
        ThrowUtils.throwIf(!workflow.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        workflow.setWorkflowIcon(updateWorkflowDTO.getWorkflowIcon());
        workflow.setWorkflowName(workflowName);
        workflow.setDescription(updateWorkflowDTO.getDescription());
        boolean update = this.updateById(workflow);
        if (!update) {
            log.error("更新工作流失败，工作流：{}", workflow);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return Boolean.TRUE;
    }



    public Page<WorkflowVO> convertPageEntity2VO(Page<Workflow> workflowPage) {
        List<Workflow> records = workflowPage.getRecords();
        List<WorkflowVO> voRecords = records.stream()
                .map(item -> {
                    WorkflowVO workflowVO = new WorkflowVO();
                    BeanUtils.copyProperties(item, workflowVO);
                    return workflowVO;
                })
                .collect(Collectors.toList());
        Page<WorkflowVO> workflowVOPage = new Page<>();
        workflowVOPage.setRecords(voRecords);
        workflowVOPage.setTotal(workflowPage.getTotal());
        workflowVOPage.setPages(workflowPage.getPages());
        workflowVOPage.setSize(workflowPage.getSize());
        return workflowVOPage;
    }
}




