package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.workflow.AddWorkflowDTO;
import com.hwq.dataloom.model.dto.workflow.QueryWorkflowDTO;
import com.hwq.dataloom.model.dto.workflow.UpdateWorkflowDTO;
import com.hwq.dataloom.model.entity.Workflow;
import com.hwq.dataloom.model.vo.workflow.WorkflowVO;
import com.hwq.dataloom.service.WorkflowService;
import com.hwq.dataloom.mapper.WorkflowMapper;
import org.springframework.stereotype.Service;

/**
* @author HWQ
* @description 针对表【workflow(工作流表)】的数据库操作Service实现
* @createDate 2024-11-18 13:41:53
*/
@Service
public class WorkflowServiceImpl extends ServiceImpl<WorkflowMapper, Workflow>
    implements WorkflowService{


    // TODO；实现impl
    @Override
    public Page<WorkflowVO> selectPage(QueryWorkflowDTO queryWorkflowDTO, User loginUser) {
        return null;
    }

    @Override
    public Workflow addWorkflow(AddWorkflowDTO addWorkflowRequest, User loginUser) {
        return null;
    }

    @Override
    public Boolean updateWorkflow(UpdateWorkflowDTO updateWorkflowDTO, User loginUser) {
        return null;
    }
}




