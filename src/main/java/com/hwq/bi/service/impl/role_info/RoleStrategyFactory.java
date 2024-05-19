package com.hwq.bi.service.impl.role_info;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HWQ
 * @date 2024/5/19 21:14
 * @description
 */
@Component
public class RoleStrategyFactory {
    @Resource
    private List<RoleService> roleServiceList;

    /**
     * 获取对应策略实现类
     * @param userType
     * @return
     */
    public RoleService getRoleStrategy(String userType) {
        return roleServiceList.stream().filter(role -> role.isCurrentRole(userType)).findFirst().orElse(null);
    }
}
