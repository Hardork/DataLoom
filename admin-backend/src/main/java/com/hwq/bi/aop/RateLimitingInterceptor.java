package com.hwq.bi.aop;

import com.hwq.bi.annotation.RateLimiter;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.manager.RedisLimiterManager;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.service.UserService;
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
