package com.hwq.dataloom.config;

import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.service.InnerUserServiceInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author HWQ
 * @date 2024/8/31 21:33
 * @description 将用户信息放到ThreadLocal中，供后续操作使用
 */
@Configuration
public class UserInterceptor implements HandlerInterceptor {

    @Resource
    private InnerUserServiceInterface userService;


    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        User loginUser = userService.getLoginUser(request);
        // 用户属于非核心功能，这里先通过模拟的形式代替。后续如果需要后管展示，会重构该代码
        UserContext.setUser(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception exception) throws Exception {
        UserContext.removeUser();
    }

}
