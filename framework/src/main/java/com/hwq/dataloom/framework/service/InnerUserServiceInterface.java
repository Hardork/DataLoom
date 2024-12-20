package com.hwq.dataloom.framework.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hwq.dataloom.framework.model.entity.User;


/**
 * @author HWQ
 * @date 2024/8/13 00:10
 * @description
 */
public interface InnerUserServiceInterface {

    User getById(Long id);

    Boolean updateUserTotalRewardPoint(Long userId, Long totalRewardPoint);

    Boolean update(UpdateWrapper<User> userQueryWrapper);

}
