package com.hwq.dataloom.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.service.InnerUserServiceInterface;
import com.hwq.dataloom.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author HWQ
 * @date 2024/8/13 14:12
 * @description
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserServiceInterface {

    @Resource
    private UserService userService;

    @Override
    public User getLoginUser(HttpServletRequest request) {
        return userService.getLoginUser(request);
    }

    @Override
    public User getById(Long id) {
        return userService.getById(id);
    }

    @Override
    public Boolean updateUserTotalRewardPoint(Long userId, Long totalRewardPoint) {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", userId);
        userUpdateWrapper.set("totalRewardPoints", totalRewardPoint);
        return userService.update(userUpdateWrapper);
    }

    @Override
    public Boolean update(UpdateWrapper<User> userQueryWrapper) {
        return userService.update(userQueryWrapper);
    }

    @Override
    public List<User> findUsersByBatch(int offset, int batchSize) {
        // TODO: 批量返回用户信息
        return null;
    }
}
