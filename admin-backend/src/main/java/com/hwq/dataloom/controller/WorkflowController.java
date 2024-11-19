package com.hwq.dataloom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.workflow.AddWorkflowDTO;
import com.hwq.dataloom.model.dto.workflow.QueryWorkflowDTO;
import com.hwq.dataloom.model.dto.workflow.SaveWorkflowDTO;
import com.hwq.dataloom.model.dto.workflow.UpdateWorkflowDTO;
import com.hwq.dataloom.model.entity.Workflow;
import com.hwq.dataloom.model.vo.workflow.WorkflowVO;
import com.hwq.dataloom.service.UserService;
import com.hwq.dataloom.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 工作流控制层
 */
@RestController
@RequestMapping("/workflow")
@Slf4j
public class WorkflowController {
    @Resource
    private WorkflowService workflowService;

    @Resource
    private UserService userService;


    @PostMapping("/draft")
    @Operation(summary = "保存工作流画布草稿")
    public BaseResponse<Long> saveWorkflowDraft(
            @RequestBody SaveWorkflowDTO saveWorkflowDTO,
            HttpServletRequest request)
    {
        if (saveWorkflowDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // TODO：校验saveWorkflowDTO内的属性
        User loginUser = userService.getLoginUser(request);
        // TODO：给workflow新增一个uniqueHash字段，实现workflow的自动保存
        Workflow workflow = workflowService.saveWorkflowDraft(saveWorkflowDTO, loginUser);
        return ResultUtils.success(workflow.getWorkflowId());
    }

    @GetMapping("/draft/{workflowId}")
    @Operation(summary = "获取工作流画布草稿")
    public BaseResponse<Long> getWorkflowDraft(
            @PathVariable("workflowId") Long workflowId,
            HttpServletRequest request)
    {
        if (workflowId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // TODO：实现workflow的查询
        Workflow workflow = workflowService.getWorkflowDraft(workflowId, loginUser);
        return ResultUtils.success(workflow.getWorkflowId());
    }

    @PostMapping("/add")
    @Operation(summary = "创建工作流")
    public BaseResponse<Long> addWorkflow(@RequestBody AddWorkflowDTO addWorkflowRequest, HttpServletRequest request) {
        if (addWorkflowRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Workflow workflow = workflowService.addWorkflow(addWorkflowRequest, loginUser);
        return ResultUtils.success(workflow.getWorkflowId());
    }


    @GetMapping("/list")
    @Operation(summary = "列表查询工作流")
    public BaseResponse<Page<WorkflowVO>> listWorkflow(QueryWorkflowDTO queryWorkflowDTO, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long size = queryWorkflowDTO.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<WorkflowVO> workflowVOPage = workflowService.selectPage(queryWorkflowDTO, loginUser);
        return ResultUtils.success(workflowVOPage);
    }

    @PostMapping("/update")
    @Operation(summary = "更新工作流")
    public BaseResponse<Boolean> updateWorkflow(@RequestBody UpdateWorkflowDTO updateWorkflowDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(updateWorkflowDTO == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(updateWorkflowDTO.getWorkflowId() == null, ErrorCode.PARAMS_ERROR, "id不得为空");
        User loginUser = userService.getLoginUser(request);
        Boolean updated = workflowService.updateWorkflow(updateWorkflowDTO, loginUser);
        return ResultUtils.success(updated);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除工作流")
    public BaseResponse<Boolean> deleteWorkflow(@PathVariable("id") Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "id参数错误");
        User user = userService.getLoginUser(request);
        Workflow workflow = workflowService.getById(id);
        // 判断是否存在
        ThrowUtils.throwIf(workflow == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!workflow.getUserId().equals(user.getId()), ErrorCode.NO_AUTH_ERROR);
        boolean b = workflowService.removeById(id);
        if (!b) {
            log.error("系统异常：删除工作流失败");
        }
        return ResultUtils.success(b);
    }
}
