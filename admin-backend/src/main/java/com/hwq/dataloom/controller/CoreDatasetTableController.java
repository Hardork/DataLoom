package com.hwq.dataloom.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.model.entity.CoreDatasourceTask;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.CoreDatasourceTaskService;
import com.hwq.dataloom.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/admin/datasetTable")
@Slf4j
public class CoreDatasetTableController {

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private UserService userService;

    @Resource
    private CoreDatasourceTaskService coreDatasourceTaskService;


    @GetMapping("/getByDatasource")
    public BaseResponse<CoreDatasourceTask> getByDatasource(Long datasourceId, HttpServletRequest httpServletRequest){
        // 鉴权
        CoreDatasource coreDatasource = coreDatasourceService.getById(datasourceId);
        Long userId = coreDatasource.getUserId();
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long loginUserId = loginUser.getId();
        if (!userId.equals(loginUserId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        QueryWrapper<CoreDatasourceTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("datasourceId",datasourceId);
        CoreDatasourceTask datasourceTask = coreDatasourceTaskService.getOne(queryWrapper);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(datasourceTask), ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(datasourceTask);
    }

}
