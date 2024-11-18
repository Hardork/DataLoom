package com.hwq.dataloom.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.workflow.AddWorkflowDTO;
import com.hwq.dataloom.model.dto.workflow.QueryWorkflowDTO;
import com.hwq.dataloom.model.dto.workflow.UpdateWorkflowDTO;
import com.hwq.dataloom.model.entity.Workflow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.model.vo.workflow.WorkflowVO;

/**
* @author HWQ
* @description 针对表【workflow(工作流表)】的数据库操作Service
* @createDate 2024-11-18 13:41:53
*/
public interface WorkflowService extends IService<Workflow> {

    Page<WorkflowVO> selectPage(QueryWorkflowDTO queryWorkflowDTO, User loginUser);

    Workflow addWorkflow(AddWorkflowDTO addWorkflowRequest, User loginUser);

    Boolean updateWorkflow(UpdateWorkflowDTO updateWorkflowDTO, User loginUser);
}
