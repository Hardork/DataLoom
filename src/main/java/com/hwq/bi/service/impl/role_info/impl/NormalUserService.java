package com.hwq.bi.service.impl.role_info.impl;

import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.enums.UserRoleEnum;
import com.hwq.bi.service.impl.role_info.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @Author:HWQ
 * @DateTime:2023/11/13 20:29
 * @Description: 普通用户策略
 **/
@Service
public class NormalUserService implements RoleService {
    @Override
    public boolean isCurrentRole(String userType) {
        ThrowUtils.throwIf(StringUtils.isEmpty(userType), ErrorCode.PARAMS_ERROR);
        return UserRoleEnum.USER.getValue().equals(userType);
    }

    @Override
    public Integer getDayReward() {
        return 10;
    }

    public Integer maxUploadFileSizeMB() {
        return 10;
    }

    public String goToQueueTag() {
        return "normal";
    }

    @Override
    public Integer getMaxToken() {
        return 2048;
    }

    @Override
    public Integer getChartSaveDay() {
        return 10;
    }

    @Override
    public Integer getChatSaveDay() {
        return 10;
    }
}
