package com.hwq.dataloom.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.model.dto.ai_role.AiRoleQueryRequest;
import com.hwq.dataloom.model.entity.UserCreateAssistant;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author HWQ
* @description 针对表【user_create_assistant(用户创建的助手)】的数据库操作Service
* @createDate 2023-10-06 17:21:13
*/
public interface UserCreateAssistantService extends IService<UserCreateAssistant> {

    QueryWrapper<UserCreateAssistant> getQueryWrapper(AiRoleQueryRequest aiRoleQueryRequest, HttpServletRequest request);

    void validAiRole(UserCreateAssistant userCreateAssistant, boolean b);
}
