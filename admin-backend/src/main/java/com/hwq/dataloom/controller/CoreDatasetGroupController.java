package com.hwq.dataloom.controller;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.newdatasource.CoreDatasetGroupDTO;
import com.hwq.dataloom.model.dto.newdatasource.UnionDTO;
import com.hwq.dataloom.model.entity.CoreDatasetGroup;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.service.CoreDatasetGroupService;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.UserService;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据集接口
 */
@RestController
@RequestMapping("/admin/coreDatasetGroup")
@Slf4j
@Tag(name = "数据集接口")
public class CoreDatasetGroupController {

    @Resource
    private DatasourceEngine datasourceEngine;

    @Resource
    private CoreDatasetGroupService coreDatasetGroupService;

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private UserService userService;

    private Lock lock = new ReentrantLock();

    @Operation(summary = "保存数据集")
    @PostMapping("/save")
    public BaseResponse<CoreDatasetGroupDTO> save(@RequestBody CoreDatasetGroupDTO coreDatasetGroupDTO, boolean rename, HttpServletRequest httpServletRequest){
        lock.lock();
        try {
            // 鉴权
            List<UnionDTO> union = coreDatasetGroupDTO.getUnion();
            UnionDTO unionDTO = union.get(0);
            ThrowUtils.throwIf(!isAuth(unionDTO, userService.getLoginUser(httpServletRequest)),ErrorCode.NO_AUTH_ERROR);
            // 如果是重命名获取pid
            if (ObjectUtils.isEmpty(coreDatasetGroupDTO.getPid()) && ObjectUtils.isNotEmpty(coreDatasetGroupDTO.getId())) {
                CoreDatasetGroup coreDatasetGroup = coreDatasetGroupService.getById(coreDatasetGroupDTO.getId());
                coreDatasetGroupDTO.setPid(coreDatasetGroup.getPid());
            }
            if (StringUtils.equalsIgnoreCase(coreDatasetGroupDTO.getNodeType(), "dataset")) {
                if (!rename && ObjectUtils.isEmpty(coreDatasetGroupDTO.getAllFields())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR);
                }
                // 获取sql
                String sql = coreDatasetGroupService.getDarasetGroupSql(coreDatasetGroupDTO);
                coreDatasetGroupDTO.setSql(sql);
                coreDatasetGroupDTO.setUnionSql(sql);
                coreDatasetGroupDTO.setInfo(Objects.requireNonNull(JSONUtil.toJsonStr(coreDatasetGroupDTO.getUnion())));
            }
            coreDatasetGroupDTO.setPid(coreDatasetGroupDTO.getPid() == null ? 0L : coreDatasetGroupDTO.getPid());
            CoreDatasetGroup coreDatasetGroup = new CoreDatasetGroup();
            BeanUtils.copyProperties(coreDatasetGroupDTO, coreDatasetGroup);
            coreDatasetGroupService.saveOrUpdate(coreDatasetGroup);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"保存数据集失败！");
        } finally {
            lock.unlock();
        }
        return ResultUtils.success(coreDatasetGroupDTO);
    }

    @Operation(summary = "获取数据集字段信息")
    @GetMapping("/get")
    public BaseResponse<CoreDatasetGroupDTO> get(Long id, HttpServletRequest httpServletRequest) {
        CoreDatasetGroup coreDatasetGroup = coreDatasetGroupService.getById(id);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(coreDatasetGroup), ErrorCode.NOT_FOUND_ERROR);
        CoreDatasetGroupDTO coreDatasetGroupDTO = new CoreDatasetGroupDTO();
        BeanUtils.copyProperties(coreDatasetGroup, coreDatasetGroupDTO);
        // 鉴权
        List<UnionDTO> union = coreDatasetGroupDTO.getUnion();
        UnionDTO unionDTO = union.get(0);
        ThrowUtils.throwIf(!isAuth(unionDTO, userService.getLoginUser(httpServletRequest)),ErrorCode.NO_AUTH_ERROR);
        String sql = coreDatasetGroup.getUnionSql();
        TypeReference<List<UnionDTO>> listTypeReference = new TypeReference<List<UnionDTO>>() {
        };
        List<UnionDTO> unionDTOList = JSONUtil.toBean(coreDatasetGroup.getInfo(), listTypeReference.getType(), true);
        Long datasourceId = unionDTOList.get(0).getCurrentDs().getDatasourceId();
        Map<String, List> data = coreDatasetGroupService.executePreviewSQL(datasourceId, sql);
        // 单独处理fields
        data.put("fields", coreDatasetGroupDTO.getAllFields());
        coreDatasetGroupDTO.setData(data);
        return ResultUtils.success(coreDatasetGroupDTO);
    }

    @Operation(summary = "预览自定义SQL")
    @PostMapping("/previewSql")
    public BaseResponse<Map<String, List>> previewSql(Long datasourceId, String sql, HttpServletRequest httpServletRequest) {
        // 鉴权
        CoreDatasource coreDatasource = coreDatasourceService.getById(datasourceId);
        Long userId = coreDatasource.getUserId();
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long loginUserId = loginUser.getId();
        if (!userId.equals(loginUserId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Map<String, List> data = coreDatasetGroupService.executePreviewSQL(datasourceId, sql);
        return ResultUtils.success(data);
    }

    /**
     * 自定义SQL鉴权
     * @param unionDTO
     * @param loginUser
     * @return
     */
    private boolean isAuth(UnionDTO unionDTO, User loginUser) {
        Long datasourceId = unionDTO.getCurrentDs().getDatasourceId();
        CoreDatasource coreDatasource = coreDatasourceService.getById(datasourceId);
        Long userId = coreDatasource.getUserId();
        Long loginUserId = loginUser.getId();
        if (!userId.equals(loginUserId)) {
            return false;
        }
        if (unionDTO.getChildrenDs() != null) {
            for (UnionDTO child : unionDTO.getChildrenDs())
                if (!isAuth(child, loginUser))
                    return false;
        }
        return true;
    }
}
