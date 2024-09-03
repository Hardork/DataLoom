package com.hwq.dataloom.service.impl.role_info.impl;

import com.hwq.dataloom.mq.producer.AnalysisMessageProducer;
import com.hwq.dataloom.mq.constant.AnalysisMqConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.enums.UserRoleEnum;
import com.hwq.dataloom.service.impl.role_info.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author:HWQ
 * @DateTime:2023/11/13 20:44
 * @Description:
 **/
@Service
public class VIPUserService implements RoleService {

    @Resource
    private AnalysisMessageProducer analysisMessageProducer;

    @Override
    public boolean isCurrentRole(String userType) {
        ThrowUtils.throwIf(StringUtils.isEmpty(userType), ErrorCode.PARAMS_ERROR);
        return UserRoleEnum.VIP.getValue().equals(userType);
    }

    @Override
    public Integer getDayReward() {
        return 20;
    }

    @Override
    public Integer getMaxToken() {
        return 2048;
    }

    @Override
    public String goToQueueName() {
        return AnalysisMqConstant.BI_VIP_QUEUE_NAME;
    }

    @Override
    public String RoutingKey() {
        return AnalysisMqConstant.BI_VIP_ROUTING_KEY;
    }

    @Override
    public void sendMessageToMQ(String message) {
        analysisMessageProducer.sendMessage(message, AnalysisMqConstant.BI_VIP_QUEUE_NAME, AnalysisMqConstant.BI_VIP_ROUTING_KEY);
    }

    @Override
    public Integer maxUploadFileSizeMB() {
        return 10;
    }

    @Override
    public Integer getChartSaveDay() {
        return 30;
    }

    @Override
    public Integer getChatSaveDay() {
        return 30;
    }
}
