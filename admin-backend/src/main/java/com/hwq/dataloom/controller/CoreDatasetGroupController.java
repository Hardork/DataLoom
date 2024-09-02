package com.hwq.dataloom.controller;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.newdatasource.CoreDatasetGroupDTO;
import com.hwq.dataloom.model.dto.newdatasource.UnionDTO;
import com.hwq.dataloom.model.entity.CoreDatasetGroup;
import com.hwq.dataloom.service.CoreDatasetGroupService;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
@RequestMapping("/coreDatasetGroup")
@Slf4j
public class CoreDatasetGroupController {

    @Resource
    private DatasourceEngine datasourceEngine;

    @Resource
    private CoreDatasetGroupService coreDatasetGroupService;

    private Lock lock = new ReentrantLock();

    @PostMapping("/save")
    public BaseResponse<CoreDatasetGroupDTO> save(@RequestBody CoreDatasetGroupDTO coreDatasetGroupDTO, boolean rename){
        lock.lock();
        try {
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

    @GetMapping("/get")
    public BaseResponse<CoreDatasetGroupDTO> get(Long id) {
        CoreDatasetGroup coreDatasetGroup = coreDatasetGroupService.getById(id);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(coreDatasetGroup), ErrorCode.NOT_FOUND_ERROR);
        CoreDatasetGroupDTO coreDatasetGroupDTO = new CoreDatasetGroupDTO();
        BeanUtils.copyProperties(coreDatasetGroup, coreDatasetGroupDTO);
        String sql = coreDatasetGroup.getUnionSql();
        TypeReference<List<UnionDTO>> listTypeReference = new TypeReference<List<UnionDTO>>() {
        };
        List<UnionDTO> unionDTOList = JSONUtil.toBean(coreDatasetGroup.getInfo(), listTypeReference.getType(), true);
        Long datasourceId = unionDTOList.get(0).getCurrentDs().getDatasourceId();
        // TODO 限制数据为100条
        ResultSet resultSet = datasourceEngine.execSelectSql(datasourceId, sql);
        List<Map<String, Object>> datas;
        try {
            datas = datasourceEngine.resultSetToMapList(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Map<String, List> data = new HashMap<>();
        data.put("fields", coreDatasetGroupDTO.getAllFields());
        data.put("data", datas);
        coreDatasetGroupDTO.setData(data);
        return ResultUtils.success(coreDatasetGroupDTO);
    }
}
