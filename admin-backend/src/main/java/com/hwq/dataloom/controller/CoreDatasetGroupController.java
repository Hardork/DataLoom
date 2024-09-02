package com.hwq.dataloom.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据集接口
 */
@RestController
@RequestMapping("/coreDatasetGroup")
@Slf4j
public class CoreDatasetGroupController {

//    @Resource
//    private CoreDatasetGroupService coreDatasetGroupService;

    private Lock lock = new ReentrantLock();

//    @PostMapping("/save")
//    public BaseResponse<CoreDatasetGroupDTO> save(CoreDatasetGroupDTO coreDatasetGroupDTO,boolean rename){
//        lock.lock();
//        try {
//            // 如果是重命名获取pid
//            if (ObjectUtils.isEmpty(coreDatasetGroupDTO.getPid()) && ObjectUtils.isNotEmpty(coreDatasetGroupDTO.getId())) {
//                CoreDatasetGroup coreDatasetGroup = coreDatasetGroupService.getById(coreDatasetGroupDTO.getId());
//                coreDatasetGroupDTO.setPid(coreDatasetGroup.getPid());
//            }
//
//            if (StringUtils.equalsIgnoreCase(coreDatasetGroupDTO.getNodeType(), "dataset")) {
//                if (!rename && ObjectUtils.isEmpty(coreDatasetGroupDTO.getAllFields())) {
//                    throw new BusinessException(ErrorCode.PARAMS_ERROR);
//                }
//                // TODO 获取unionsql
//
//            }
//            coreDatasetGroupDTO.setPid(coreDatasetGroupDTO.getPid() == null ? 0L : coreDatasetGroupDTO.getPid());
//            CoreDatasetGroup coreDatasetGroup = new CoreDatasetGroup();
//            BeanUtils.copyProperties(coreDatasetGroupDTO, coreDatasetGroup);
//            coreDatasetGroupService.saveOrUpdate(coreDatasetGroup);
//        } catch (Exception e) {
//            throw new BusinessException(ErrorCode.OPERATION_ERROR,"保存数据集失败！");
//        } finally {
//            lock.unlock();
//        }
//        return ResultUtils.success(coreDatasetGroupDTO);
//    }

}
