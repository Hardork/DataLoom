package com.hwq.bi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.bi.model.dto.ai_role.AiRoleQueryRequest;
import com.hwq.bi.model.entity.AiRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HWQ
* @description 针对表【ai_role】的数据库操作Service
* @createDate 2023-09-27 15:14:34
*/
public interface AiRoleService extends IService<AiRole> {

    void validAiRole(AiRole aiRole, boolean b);

    QueryWrapper<AiRole> getQueryWrapper(AiRoleQueryRequest aiRoleQueryRequest);
}
