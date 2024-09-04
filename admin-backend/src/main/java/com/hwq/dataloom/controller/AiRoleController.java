package com.hwq.dataloom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwq.dataloom.annotation.AuthCheck;
import com.hwq.dataloom.framework.constants.UserConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.request.DeleteRequest;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.ai_role.AiRoleAddRequest;
import com.hwq.dataloom.model.dto.ai_role.AiRoleQueryRequest;
import com.hwq.dataloom.model.dto.ai_role.AiRoleUpdateRequest;

import com.hwq.dataloom.model.entity.AiRole;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.service.AiRoleService;
import com.hwq.dataloom.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/Hardork">老山羊</a>
 * 
 */
@RestController
@RequestMapping("/admin/aiRole")
@Slf4j
public class AiRoleController {

    @Resource
    private AiRoleService aiRoleService;

    @Resource
    private UserService userService;

    /**
     * 创建
     *
     * @param aiRoleAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addAiRole(@RequestBody AiRoleAddRequest aiRoleAddRequest, HttpServletRequest request) {
        if (aiRoleAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AiRole aiRole = new AiRole();
        BeanUtils.copyProperties(aiRoleAddRequest, aiRole);
        aiRoleService.validAiRole(aiRole, true);
        User loginUser = userService.getLoginUser(request);
        aiRole.setUserId(loginUser.getId());
        // boolean转换为int
        if (aiRoleAddRequest.getHistoryTalk()) {
            aiRole.setHistoryTalk(1);
        } else {
            aiRole.setHistoryTalk(0);
        }
        boolean result = aiRoleService.save(aiRole);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newAiRoleId = aiRole.getId();
        return ResultUtils.success(newAiRoleId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAiRole(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        AiRole oldAiRole = aiRoleService.getById(id);
        ThrowUtils.throwIf(oldAiRole == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = aiRoleService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param aiRoleUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAiRole(@RequestBody AiRoleUpdateRequest aiRoleUpdateRequest) {
        if (aiRoleUpdateRequest == null || aiRoleUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 参数校验
        long id = aiRoleUpdateRequest.getId();
        // 判断是否存在
        AiRole oldAiRole = aiRoleService.getById(id);
        ThrowUtils.throwIf(oldAiRole == null, ErrorCode.NOT_FOUND_ERROR);
        BeanUtils.copyProperties(aiRoleUpdateRequest, oldAiRole);
        boolean result = aiRoleService.updateById(oldAiRole);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<AiRole> getAiRoleById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AiRole aiRole = aiRoleService.getById(id);
        if (aiRole == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(aiRole);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param aiRoleQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AiRole>> listAiRoleVOByPage(@RequestBody AiRoleQueryRequest aiRoleQueryRequest,
            HttpServletRequest request) {
        long current = aiRoleQueryRequest.getCurrent();
        long size = aiRoleQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<AiRole> postPage = aiRoleService.page(new Page<>(current, size),
                aiRoleService.getQueryWrapper(aiRoleQueryRequest));
        return ResultUtils.success(postPage);
    }

}
