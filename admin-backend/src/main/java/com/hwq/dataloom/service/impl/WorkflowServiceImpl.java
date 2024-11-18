package com.hwq.dataloom.service.impl;

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
import com.hwq.dataloom.model.enums.WorkflowTypeEnum;
import com.hwq.dataloom.model.vo.workflow.WorkflowVO;
import com.hwq.dataloom.service.WorkflowService;
import com.hwq.dataloom.mapper.WorkflowMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public Workflow saveWorkflowDraft(SaveWorkflowDTO saveWorkflowDTO, User loginUser) {
        return null;
    }

    @Override
    public Workflow getWorkflowDraft(Long workflowId, User loginUser) {
        return null;
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




