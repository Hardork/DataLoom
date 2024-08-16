package com.hwq.dataloom.aop;

import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.enums.ServiceTypeEnums;
import com.hwq.dataloom.service.ServiceRecordService;
import com.hwq.dataloom.service.UserService;
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

    @AfterReturning(pointcut = "@annotation(com.hwq.dataloom.annotation.AiService)")
    public void saveAiServiceRecord(JoinPoint joinPoint) {
        // 获取用户信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);

        // 保存记录
        com.hwq.dataloom.model.entity.ServiceRecord serviceRecord = new com.hwq.dataloom.model.entity.ServiceRecord();
        serviceRecord.setUserId(loginUser.getId());
        serviceRecord.setType(Integer.parseInt(ServiceTypeEnums.AI.getValue() + ""));
        boolean save = service.save(serviceRecord);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
    }

    @AfterReturning(pointcut = "@annotation(com.hwq.dataloom.annotation.BiService)")
    public void saveBiServiceRecord(JoinPoint joinPoint) {
        // 获取用户信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);

        // 保存记录
        com.hwq.dataloom.model.entity.ServiceRecord serviceRecord = new com.hwq.dataloom.model.entity.ServiceRecord();
        serviceRecord.setUserId(loginUser.getId());
        serviceRecord.setType(Integer.parseInt(ServiceTypeEnums.BI.getValue() + ""));
        boolean save = service.save(serviceRecord);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
    }
}
