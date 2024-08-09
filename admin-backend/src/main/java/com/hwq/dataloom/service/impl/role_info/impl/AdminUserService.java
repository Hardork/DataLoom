package com.hwq.dataloom.service.impl.role_info.impl;

import com.hwq.dataloom.bizmq.BiMessageProducer;
import com.hwq.dataloom.bizmq.BiMqConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.enums.UserRoleEnum;
import com.hwq.dataloom.service.impl.role_info.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author:HWQ
 * @DateTime:2023/11/13 20:47
 * @Description:
 **/
@Service
public class AdminUserService implements RoleService {
    @Resource
    private BiMessageProducer biMessageProducer;

    @Override
    public boolean isCurrentRole(String userType) {
        ThrowUtils.throwIf(StringUtils.isEmpty(userType), ErrorCode.PARAMS_ERROR);
        return UserRoleEnum.ADMIN.getValue().equals(userType);
    }

    @Override
    public Integer getDayReward() {
        return 40;
    }

    @Override
    public Integer getMaxToken() {
        return 4096;
    }


    @Override
    public String goToQueueName() {
        return BiMqConstant.BI_VIP_QUEUE_NAME;
    }

    @Override
    public String RoutingKey() {
        return BiMqConstant.BI_VIP_ROUTING_KEY;
    }

    @Override
    public void sendMessageToMQ(String message) {
        biMessageProducer.sendMessage(message, BiMqConstant.BI_VIP_EXCHANGE_NAME, BiMqConstant.BI_VIP_ROUTING_KEY);
    }

    @Override
    public Integer maxUploadFileSizeMB() {
        return 10;
    }

    @Override
    public Integer getChartSaveDay() {
        return 60;
    }

    @Override
    public Integer getChatSaveDay() {
        return 60;
    }
}
