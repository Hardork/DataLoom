package com.hwq.bi.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.constant.CommonConstant;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.dto.ai_role.AiRoleQueryRequest;
import com.hwq.bi.model.entity.AiRole;
import com.hwq.bi.service.AiRoleService;
import com.hwq.bi.mapper.AiRoleMapper;
import com.hwq.bi.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author HWQ
* @description 针对表【ai_role】的数据库操作Service实现
* @createDate 2023-09-27 15:14:34
*/
@Service
public class AiRoleServiceImpl extends ServiceImpl<AiRoleMapper, AiRole>
    implements AiRoleService{

    @Override
    public void validAiRole(AiRole aiRole, boolean b) {
         String assistantName = aiRole.getAssistantName();
         String type = aiRole.getType();
         Integer historyTalk = aiRole.getHistoryTalk();
         String functionDes = aiRole.getFunctionDes();
         String inputModel = aiRole.getInputModel();
        // 校验参数
        ThrowUtils.throwIf(StringUtils.isEmpty(assistantName), ErrorCode.PARAMS_ERROR, "助手名称不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(type), ErrorCode.PARAMS_ERROR, "助手类型不得为空");
        ThrowUtils.throwIf(historyTalk != null, ErrorCode.PARAMS_ERROR, "历史对话不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(functionDes), ErrorCode.PARAMS_ERROR, "功能描述不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(inputModel), ErrorCode.PARAMS_ERROR, "输入模板不得为空");
    }

    @Override
    public QueryWrapper<AiRole> getQueryWrapper(AiRoleQueryRequest aiRoleQueryRequest) {
         Long id = aiRoleQueryRequest.getId();
         String assistantName = aiRoleQueryRequest.getAssistantName();
         String type = aiRoleQueryRequest.getType();
         Integer historyTalk = aiRoleQueryRequest.getHistoryTalk();
         String functionDes = aiRoleQueryRequest.getFunctionDes();
         String inputModel = aiRoleQueryRequest.getInputModel();
         String roleDesign = aiRoleQueryRequest.getRoleDesign();
         String targetWork = aiRoleQueryRequest.getTargetWork();
         String requirement = aiRoleQueryRequest.getRequirement();
         String style = aiRoleQueryRequest.getStyle();
         String otherRequire = aiRoleQueryRequest.getOtherRequire();
         String sortField = aiRoleQueryRequest.getSortField();
         String sortOrder = aiRoleQueryRequest.getSortOrder();

        QueryWrapper<AiRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(StringUtils.isNotEmpty(assistantName), "assistantName", assistantName);
        queryWrapper.eq(StringUtils.isNotEmpty(type), "type", type);
        queryWrapper.eq(StringUtils.isNotEmpty(functionDes), "functionDes", functionDes);
        queryWrapper.eq(StringUtils.isNotEmpty(inputModel), "inputModel", inputModel);
        queryWrapper.eq(StringUtils.isNotEmpty(roleDesign), "roleDesign", roleDesign);
        queryWrapper.eq(StringUtils.isNotEmpty(inputModel), "inputModel", inputModel);
        queryWrapper.eq(ObjectUtils.isNotEmpty(historyTalk), "historyTalk", historyTalk);
        queryWrapper.eq(StringUtils.isNotEmpty(targetWork), "targetWork", targetWork);
        queryWrapper.eq(StringUtils.isNotEmpty(requirement), "requirement", requirement);
        queryWrapper.eq(StringUtils.isNotEmpty(style), "style", style);
        queryWrapper.eq(StringUtils.isNotEmpty(otherRequire), "otherRequire", otherRequire);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




