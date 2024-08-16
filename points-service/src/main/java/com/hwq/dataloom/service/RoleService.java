package com.hwq.dataloom.service;

/**
 * @Author:HWQ
 * @DateTime:2023/11/13 20:31
 * @Description: 角色策略接口
 **/
public interface RoleService {
    /**
     * 判断是否是当前角色
     * @return
     */
    boolean isCurrentRole(String userType);

    /**
     * 获取每日积分的数量
     * @return
     */
    Integer getDayReward();
}
