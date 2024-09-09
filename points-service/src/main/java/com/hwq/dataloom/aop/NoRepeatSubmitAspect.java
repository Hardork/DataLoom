package com.hwq.dataloom.aop;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.hwq.dataloom.annotation.NoRepeatSubmit;
import com.hwq.dataloom.config.UserContext;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static com.hwq.dataloom.constants.CommonConstant.REPEAT_SUBMIT_LOCK_KEY_TEMPLATE;

/**
 * @author HWQ
 * @date 2024/8/27 13:08
 * @description
 */
@Aspect
@RequiredArgsConstructor
public class NoRepeatSubmitAspect {
    private final RedissonClient redissonClient;

    /**
     * 增强方法标记 {@link NoRepeatSubmit} 注解逻辑
     */
    @Around("@annotation(com.hwq.dataloom.annotation.NoRepeatSubmit)")
    public Object noRepeatSummit(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解上的信息
        NoRepeatSubmit noRepeatSubmitAnnotation = getNoRepeatSubmitAnnotation(joinPoint);
        String lock_key = String.format(REPEAT_SUBMIT_LOCK_KEY_TEMPLATE, getServletPath(), getCurrentUserInfo().getId(), calcArgsMD5(joinPoint));
        RLock lock = redissonClient.getLock(lock_key);
        if (!lock.tryLock()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, noRepeatSubmitAnnotation.message());
        }
        Object result;
        try {
            // 执行标记了防重复提交注解的方法原逻辑
            result = joinPoint.proceed();
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * @return 返回自定义防重复提交注解
     */
    public NoRepeatSubmit getNoRepeatSubmitAnnotation(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        return targetMethod.getAnnotation(NoRepeatSubmit.class);
    }

    /**
     * @return 获取当前线程上下文 ServletPath
     */
    private String getServletPath() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return sra.getRequest().getServletPath();
    }


    public User getCurrentUserInfo() {
        // TODO：将获取用户信息改造为ThreadLocal存储，无需每一次都去RPC请求用户信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = UserContext.getUser();
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return loginUser;
    }

    /**
     * 对参数进行哈希
     * function：用于判断参数是否相同
     * @return joinPoint md5
     */
    private String calcArgsMD5(ProceedingJoinPoint joinPoint) {
        return DigestUtil.md5Hex(JSON.toJSONBytes(joinPoint.getArgs()));
    }
}
