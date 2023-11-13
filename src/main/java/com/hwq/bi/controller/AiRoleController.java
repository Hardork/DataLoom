package com.hwq.bi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.hwq.bi.annotation.AuthCheck;
import com.hwq.bi.common.BaseResponse;
import com.hwq.bi.common.DeleteRequest;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.common.ResultUtils;
import com.hwq.bi.constant.UserConstant;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.dto.ai_role.AiRoleAddRequest;
import com.hwq.bi.model.dto.ai_role.AiRoleQueryRequest;
import com.hwq.bi.model.dto.ai_role.AiRoleUpdateRequest;
import com.hwq.bi.model.dto.post.PostAddRequest;
import com.hwq.bi.model.dto.post.PostEditRequest;
import com.hwq.bi.model.dto.post.PostQueryRequest;
import com.hwq.bi.model.dto.post.PostUpdateRequest;
import com.hwq.bi.model.entity.AiRole;
import com.hwq.bi.model.entity.Post;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.vo.PostVO;
import com.hwq.bi.service.AiRoleService;
import com.hwq.bi.service.PostService;
import com.hwq.bi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/Hardork">老山羊</a>
 * 
 */
@RestController
@RequestMapping("/aiRole")
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

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
//    @PostMapping("/my/list/page/vo")
//    public BaseResponse<Page<PostVO>> listMyPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
//            HttpServletRequest request) {
//        if (postQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        postQueryRequest.setUserId(loginUser.getId());
//        long current = postQueryRequest.getCurrent();
//        long size = postQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<Post> postPage = postService.page(new Page<>(current, size),
//                postService.getQueryWrapper(postQueryRequest));
//        return ResultUtils.success(postService.getPostVOPage(postPage, request));
//    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
//    @PostMapping("/search/page/vo")
//    public BaseResponse<Page<PostVO>> searchPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
//            HttpServletRequest request) {
//        long size = postQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
//        return ResultUtils.success(postService.getPostVOPage(postPage, request));
//    }

}
