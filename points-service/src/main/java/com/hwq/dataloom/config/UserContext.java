package com.hwq.dataloom.config;

import com.hwq.dataloom.framework.model.entity.User;

import java.util.Optional;

/**
 * @author HWQ
 * @date 2024/8/31 21:40
 * @description
 */
public final class UserContext {

    /**
     * <a href="https://github.com/alibaba/transmittable-thread-local" />
     */
    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置用户至上下文
     *
     * @param user 用户详情信息
     */
    public static void setUser(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取上下文中用户 ID
     *
     * @return 用户 ID
     */
    public static Long getUserId() {
        User userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO).map(User::getId).orElse(null);
    }

    /**
     * 获取上下文中用户
     *
     * @return 用户
     */
    public static User getUser() {
        return USER_THREAD_LOCAL.get();
    }


    /**
     * 清理用户上下文
     */
    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }
}