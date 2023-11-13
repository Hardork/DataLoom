package com.hwq.bi.service.impl.role_info.impl;

import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.enums.UserRoleEnum;
import com.hwq.bi.service.impl.role_info.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @Author:HWQ
 * @DateTime:2023/11/13 20:45
 * @Description:
 **/
@Service
public class SVIPUserService implements RoleService {
    @Override
    public boolean isCurrentRole(String userType) {
        ThrowUtils.throwIf(StringUtils.isEmpty(userType), ErrorCode.PARAMS_ERROR);
        return UserRoleEnum.SVIP.getValue().equals(userType);
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
    public Integer getChartSaveDay() {
        return 60;
    }

    @Override
    public Integer getChatSaveDay() {
        return 60;
    }
}
