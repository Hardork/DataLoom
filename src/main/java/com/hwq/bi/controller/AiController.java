package com.hwq.bi.controller;
import java.util.Date;

import com.hwq.bi.annotation.AiService;
import com.hwq.bi.annotation.CheckPoint;
import com.hwq.bi.annotation.ReduceRewardPoint;
import com.hwq.bi.common.BaseResponse;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.common.ResultUtils;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.manager.RedisLimiterManager;
import com.hwq.bi.manager.SparkAiManager;
import com.hwq.bi.model.dto.ai.AiChatRequest;
import com.hwq.bi.model.entity.AiRole;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.service.AiRoleService;
import com.hwq.bi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author:HWQ
 * @DateTime:2023/9/25 20:42
 * @Description:
 **/
@RestController
@Slf4j
@RequestMapping("/Ai")
public class AiController {
    @Resource
    private UserService userService;
    @Resource
    private SparkAiManager sparkAiManager;
    @Resource
    private RedisLimiterManager redisLimiterManager;
    @Resource
    private AiRoleService aiRoleService;

    @ReduceRewardPoint(reducePoint = 1)
    @CheckPoint(needPoint = 1)
    @AiService
    @PostMapping("/talk")
    public BaseResponse<Boolean> getAiTalk(@RequestBody AiChatRequest aiTalkRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(aiTalkRequest == null || StringUtils.isEmpty(aiTalkRequest.getText()), ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 校验提问字数 < 200字
        ThrowUtils.throwIf(aiTalkRequest.getText().length() > 2000, ErrorCode.PARAMS_ERROR, "提问字数过多");
        // 限流
        redisLimiterManager.doRateLimit("aiTalk_" + loginUser.getId());
        sparkAiManager.setUserId(loginUser.getId());
        sparkAiManager.startTalk(aiTalkRequest.getText());
        return ResultUtils.success(true);
    }

    @ReduceRewardPoint(reducePoint = 1)
    @CheckPoint(needPoint = 1)
    @AiService
    @PostMapping("/chat/assistant")
    public BaseResponse<Boolean> chatWithAssistant(@RequestBody AiChatRequest aiChatRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(aiChatRequest == null, ErrorCode.PARAMS_ERROR);
        String text = aiChatRequest.getText();
        Long assistantId = aiChatRequest.getAssistantId();
        ThrowUtils.throwIf(StringUtils.isEmpty(text), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(assistantId), ErrorCode.PARAMS_ERROR);
        AiRole aiRole = aiRoleService.getById(assistantId);

        User loginUser = userService.getLoginUser(request);
        // 校验提问字数 < 200字
        ThrowUtils.throwIf(aiChatRequest.getText().length() > 2000, ErrorCode.PARAMS_ERROR, "提问字数过多");
        ThrowUtils.throwIf(aiRole == null, ErrorCode.PARAMS_ERROR, "不存在该助手");
        String aiRoleInput = buildAiRoleInput(text, aiRole);

        // 限流
        redisLimiterManager.doRateLimit("aiTalk_" + loginUser.getId());
        sparkAiManager.setUserId(loginUser.getId());
        sparkAiManager.startTalk(aiRoleInput);

        return ResultUtils.success(true);
    }

    @PostMapping("/getChat")
    public BaseResponse<String> getChat(@RequestBody AiChatRequest aiChatRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(aiChatRequest == null, ErrorCode.PARAMS_ERROR);
        String text = aiChatRequest.getText();
        Long assistantId = aiChatRequest.getAssistantId();
        ThrowUtils.throwIf(StringUtils.isEmpty(text), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(assistantId), ErrorCode.PARAMS_ERROR);
        AiRole aiRole = aiRoleService.getById(assistantId);
        String aiRoleInput = buildAiRoleInput(text, aiRole);
        return ResultUtils.success(aiRoleInput);
    }



    public String buildAiRoleInput(String text, AiRole aiRole) {
        String type = aiRole.getType();
        String functionDes = aiRole.getFunctionDes();
        String inputModel = aiRole.getInputModel();
        String roleDesign = aiRole.getRoleDesign();
        String targetWork = aiRole.getTargetWork();
        String requirement = aiRole.getRequirement();
        String style = aiRole.getStyle();
        String otherRequire = aiRole.getOtherRequire();
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(type)) {
            sb.append("你是" + type + "方面的专家" + ",");
        }
        if (StringUtils.isNotEmpty(roleDesign)) {
            sb.append("角色设定:" + roleDesign + ",");
        }
        if (StringUtils.isNotEmpty(targetWork)) {
            sb.append("你的任务是" + targetWork + ",");
        }
        if (StringUtils.isNotEmpty(functionDes)) {
            sb.append("功能描述:" + functionDes + ",");
        }
        if (StringUtils.isNotEmpty(inputModel)) {
            sb.append("输入模板:" + inputModel + ",");
        }
        if (StringUtils.isNotEmpty(requirement)) {
            sb.append("需求说明:" + requirement + ",");
        }
        if (StringUtils.isNotEmpty(style)) {
            sb.append("回答风格:" + style + ",");
        }
        if (StringUtils.isNotEmpty(otherRequire)) {
            sb.append("其它需求:" + otherRequire + ",");
        }
        if (StringUtils.isNotEmpty(text)) {
            sb.append("我要问的是:" + text);
        }
        return sb.toString();
    }

//    @PostMapping("/assistant/add")
//    public BaseResponse<Boolean> addAiAssistant(@RequestBody SetAssistantRequest setAssistantRequest, HttpServletRequest request) {
//        User loginUser = userService.getLoginUser(request);
//        // 校验提问字数 < 200字
//        ThrowUtils.throwIf(aiTalkRequest.getText().length() > 2000, ErrorCode.PARAMS_ERROR, "提问字数过多");
//        // 限流
//        redisLimiterManager.doRateLimit("aiTalk_" + loginUser.getId());
//        sparkAiManager.setUserId(loginUser.getId());
//        sparkAiManager.startTalk(aiTalkRequest.getText());
//        return ResultUtils.success(true);
//    }
}
