package com.hwq.dataloom.aop;

import com.hwq.dataloom.annotation.RateLimiter;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.manager.RedisLimiterManager;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.service.UserService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author HWQ
 * @date 2024/5/13 15:26
 * @description 限流AOP
 */
@Aspect
@Component
public class RateLimitingInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private RedisLimiterManager redisLimiterManager;
    @Before("@annotation(rateLimiter)")
    public void setRateLimiter(RateLimiter rateLimiter) throws Throwable {
        int rate = rateLimiter.ratePerSecond();
        String key = rateLimiter.key();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 必须有该权限才通过
        redisLimiterManager.doRateLimit(key + loginUser.getId(), rate);
    }
}
