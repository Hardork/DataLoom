package com.hwq.dataloom.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

import cn.hutool.core.util.IdUtil;
import cn.hutool.db.sql.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.config.UserContext;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.service.InnerUserServiceInterface;
import com.hwq.dataloom.model.dto.coupon_task.CouponTaskCreateReqDTO;
import com.hwq.dataloom.model.dto.coupon_task.CouponTaskPageQueryReqDTO;
import com.hwq.dataloom.model.entity.CouponTask;
import com.hwq.dataloom.model.enums.CouponNotifyTypeEnum;
import com.hwq.dataloom.model.enums.CouponSendTypeEnum;
import com.hwq.dataloom.model.enums.CouponTaskStatusEnum;
import com.hwq.dataloom.mq.event.CouponTaskDirectEvent;
import com.hwq.dataloom.mq.producer.CouponExcelAnalysisMessageProducer;
import com.hwq.dataloom.service.CouponTaskService;
import com.hwq.dataloom.mapper.CouponTaskMapper;
import com.hwq.dataloom.service.CouponTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author wqh
* @description 针对表【coupon_task(优惠券模板发送任务表)】的数据库操作Service实现
* @createDate 2024-08-30 17:07:36
*/
@Service
public class CouponTaskServiceImpl extends ServiceImpl<CouponTaskMapper, CouponTask>
    implements CouponTaskService{

    @Resource
    private CouponExcelAnalysisMessageProducer couponExcelAnalysisMessageProducer;

    @Resource
    private CouponTemplateService couponTemplateService;


    @DubboReference
    private InnerUserServiceInterface innerUserServiceInterface;


    @Override
    public void manuallySendCouponTask(CouponTaskCreateReqDTO couponTaskCreateReqDTO) {
        // 校验创建优惠券请求参数是否合法
        checkCouponTaskCreateReqDTOValid(couponTaskCreateReqDTO);

        // 创建优惠券任务
        CouponTask couponTask = new CouponTask();
        couponTask.setBatchId(IdUtil.getSnowflakeNextId());
        couponTask.setStatus(CouponTaskStatusEnum.WAIT_EXE.getStatus());
        couponTask.setOperatorId(UserContext.getUserId());
        BeanUtils.copyProperties(couponTaskCreateReqDTO, couponTask);
        ThrowUtils.throwIf(!this.save(couponTask), ErrorCode.SYSTEM_ERROR);

        CouponSendTypeEnum sendTypeEnum = CouponSendTypeEnum.findValueByType(couponTaskCreateReqDTO.getSendType());
        // 将立即发送的任务直接推送到excel解析队列进行消费
        if (sendTypeEnum == CouponSendTypeEnum.DIRECT) {
            CouponTaskDirectEvent event = CouponTaskDirectEvent.builder()
                    .couponTaskId(couponTaskCreateReqDTO.getCouponTemplateId())
                    .build();
            couponExcelAnalysisMessageProducer.sendMessage(event);
        }
    }

    @Override
    public Page<CouponTask> pageQueryCouponTask(CouponTaskPageQueryReqDTO requestParam) {
        String batchId = requestParam.getBatchId();
        String taskName = requestParam.getTaskName();
        Long couponTemplateId = requestParam.getCouponTemplateId();
        Integer status = requestParam.getStatus();
        long current = requestParam.getCurrent();
        long pageSize = requestParam.getPageSize();
        LambdaQueryWrapper<CouponTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(batchId), CouponTask::getBatchId, batchId);
        wrapper.eq(StringUtils.isNotEmpty(taskName), CouponTask::getTaskName, taskName);
        wrapper.eq(couponTemplateId != null, CouponTask::getCouponTemplateId, couponTemplateId);
        wrapper.eq(status != null, CouponTask::getStatus, status);
        wrapper.eq(status != null, CouponTask::getStatus, status);
        return this.page(new Page<>(current, pageSize), wrapper);
    }

    private void checkCouponTaskCreateReqDTOValid(CouponTaskCreateReqDTO couponTaskCreateReqDTO) {
        String notifyType = couponTaskCreateReqDTO.getNotifyType();
        Integer sendType = couponTaskCreateReqDTO.getSendType();
        Date sendTime = couponTaskCreateReqDTO.getSendTime();
        // 校验
        CouponSendTypeEnum sendTypeEnum = CouponSendTypeEnum.findValueByType(sendType);
        ThrowUtils.throwIf(sendTypeEnum == null, ErrorCode.PARAMS_ERROR, "无对应发送类型");
        ThrowUtils.throwIf(sendTypeEnum == CouponSendTypeEnum.DELAY && sendTime == null, ErrorCode.PARAMS_ERROR, "延迟发送必须要有发送时间");
        ThrowUtils.throwIf(sendTypeEnum == CouponSendTypeEnum.DELAY && sendTime.before(new Date()), ErrorCode.PARAMS_ERROR, "延迟发送时间必须晚于当前时间");
        Integer[] notifyTypes = getNotifyType(notifyType);
        boolean hasNotifyTypes = Arrays.stream(notifyTypes)
                .anyMatch(e -> CouponNotifyTypeEnum.findValueByType(e) != null);
        ThrowUtils.throwIf(!hasNotifyTypes, ErrorCode.PARAMS_ERROR, "无对应消息通知类型");
    }

    /**
     * 获取对应的通知类型
     * @param notifyType
     * @return
     */
    private Integer[] getNotifyType(String notifyType) {
        // 组合发送短信
        String[] split = notifyType.split("-");
        // 使用 Set 去重，然后转换回 String[]
        return Arrays.stream(split)
                .distinct()
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
    }
}




