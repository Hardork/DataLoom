package com.hwq.bi.aop;

import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.enums.ServiceTypeEnums;
import com.hwq.bi.service.ServiceRecordService;
import com.hwq.bi.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author:HWQ
 * @DateTime:2023/9/29 20:38
 * @Description:
 **/
@Aspect
@Component
public class ServiceRecord {
    @Resource
    private UserService userService;
    @Resource
    private ServiceRecordService service;

    @AfterReturning(pointcut = "@annotation(com.hwq.bi.annotation.AiService)")
    public void saveAiServiceRecord(JoinPoint joinPoint) {
        // 获取用户信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);

        // 保存记录
        com.hwq.bi.model.entity.ServiceRecord serviceRecord = new com.hwq.bi.model.entity.ServiceRecord();
        serviceRecord.setUserId(loginUser.getId());
        serviceRecord.setType(Integer.parseInt(ServiceTypeEnums.AI.getValue() + ""));
        boolean save = service.save(serviceRecord);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
    }

    @AfterReturning(pointcut = "@annotation(com.hwq.bi.annotation.BiService)")
    public void saveBiServiceRecord(JoinPoint joinPoint) {
        // 获取用户信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);

        // 保存记录
        com.hwq.bi.model.entity.ServiceRecord serviceRecord = new com.hwq.bi.model.entity.ServiceRecord();
        serviceRecord.setUserId(loginUser.getId());
        serviceRecord.setType(Integer.parseInt(ServiceTypeEnums.BI.getValue() + ""));
        boolean save = service.save(serviceRecord);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
    }
}
