package com.hwq.dataloom.controller;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.annotation.AiService;
import com.hwq.dataloom.annotation.CheckPoint;
import com.hwq.dataloom.annotation.ReduceRewardPoint;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.manager.AiManager;
import com.hwq.dataloom.manager.SparkAiManager;
import com.hwq.dataloom.model.dto.ai.*;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.*;
import com.hwq.dataloom.model.enums.ChatHistoryRoleEnum;
import com.hwq.dataloom.model.vo.GetUserChatHistoryVO;
import com.hwq.dataloom.model.vo.ai.GetUserSQLChatRecordVO;
import com.hwq.dataloom.service.*;
import com.hwq.dataloom.utils.datasource.CustomPage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author:HWQ
 * @DateTime:2023/9/25 20:42
 * @Description: AI应用接口
 **/
@RestController
@Slf4j
@RequestMapping("/admin/Ai")
public class AiController {
    @Resource
    private UserService userService;
    @Resource
    private SparkAiManager sparkAiManager;
    @Resource
    private AiRoleService aiRoleService;

    @Resource
    private AIService aiService;

    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private ChatService chatService;

    @Resource
    private UserCreateAssistantService userCreateAssistantService;

    @Resource
    private CoreDatasourceService coreDatasourceService;

    /**
     * 单次会话
     * @param aiTalkRequest
     * @param request
     * @return
     */
    @ReduceRewardPoint
    @CheckPoint
    @AiService
    @PostMapping("/talk")
    public BaseResponse<Boolean> getAiTalk(@RequestBody AiChatRequest aiTalkRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(aiTalkRequest == null || StringUtils.isEmpty(aiTalkRequest.getText()), ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 校验提问字数 < 200字
        ThrowUtils.throwIf(aiTalkRequest.getText().length() > 2000, ErrorCode.PARAMS_ERROR, "提问字数过多");
        // 限流
        sparkAiManager.setUserId(loginUser.getId());
        sparkAiManager.startTalk(aiTalkRequest.getText());
        return ResultUtils.success(true);
    }

    /**
     * 与助手单次会话
     * @param aiChatRequest
     * @param request
     * @return
     */
    @ReduceRewardPoint
    @CheckPoint
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
        sparkAiManager.setUserId(loginUser.getId());
        sparkAiManager.startTalk(aiRoleInput);

        return ResultUtils.success(true);
    }

    /**
     * 与暂时保存的助手对话（调试助手）
     * @param request
     * @return
     */
    @ReduceRewardPoint
    @CheckPoint
    @AiService
    @PostMapping("/chat/temp")
    public BaseResponse<Boolean> chatWithTemp (@RequestBody AiTempChatRequest aiTempChatRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(aiTempChatRequest == null, ErrorCode.PARAMS_ERROR);
        String text = aiTempChatRequest.getText();
        ThrowUtils.throwIf(StringUtils.isEmpty(text), ErrorCode.PARAMS_ERROR);

        AiRole aiRole = new AiRole();
        BeanUtils.copyProperties(aiTempChatRequest, aiRole);
        aiRoleService.validAiRole(aiRole, true);
        User loginUser = userService.getLoginUser(request);
        // 校验提问字数 < 200字
        ThrowUtils.throwIf(text.length() > 2000, ErrorCode.PARAMS_ERROR, "提问字数过多");
        String aiRoleInput = buildAiRoleInput(text, aiRole);

        // 限流
        sparkAiManager.setUserId(loginUser.getId());
        sparkAiManager.setUnSave(true);
        sparkAiManager.startTalk(aiRoleInput);
        return ResultUtils.success(true);
    }

    @Operation(summary = "用户会话聊天")
    @ReduceRewardPoint
    @CheckPoint
    @AiService
    @PostMapping("/chat/model")
    public BaseResponse<Boolean> userChatWithModel(@RequestBody ChatWithModelRequest chatWithModelRequest, HttpServletRequest request) {
        // 数据校验
        ThrowUtils.throwIf(chatWithModelRequest == null, ErrorCode.PARAMS_ERROR);
        String text = chatWithModelRequest.getText();
        Long chatId = chatWithModelRequest.getChatId();
        ThrowUtils.throwIf(StringUtils.isEmpty(text), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(chatId), ErrorCode.PARAMS_ERROR);
        // 限流
        User loginUser = userService.getLoginUser(request);
        Chat chat = chatService.getById(chatId);
        // 校验提问字数 < 200字
        ThrowUtils.throwIf(text.length() > 2000, ErrorCode.PARAMS_ERROR, "提问字数过多");
        ThrowUtils.throwIf(chat == null, ErrorCode.PARAMS_ERROR, "不存在该助手");
        Long modelId = chat.getModelId();
        AiRole model = aiRoleService.getById(modelId);
        if (model == null) {
            UserCreateAssistant userCreateAssistant = userCreateAssistantService.getById(modelId);
            model = new AiRole();
            BeanUtils.copyProperties(userCreateAssistant, model);
        }
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setChatRole(ChatHistoryRoleEnum.USER.getValue());
        chatHistory.setChatId(chatId);
        chatHistory.setModelId(modelId);
        chatHistory.setContent(text);
        boolean save = chatHistoryService.save(chatHistory);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        String aiRoleInput = buildAiRoleInput(text, model);
        sparkAiManager.setUserId(loginUser.getId());
        sparkAiManager.setChatId(chatId);
        sparkAiManager.setModelId(modelId);
        sparkAiManager.startTalk(aiRoleInput);
        return ResultUtils.success(true);
    }

    @Operation(summary = "智能问数")
    @ReduceRewardPoint
    @CheckPoint
    @AiService
    @PostMapping("/chat/sql")
    public BaseResponse<Boolean> userChatForSQL(@RequestBody ChatForSQLRequest chatForSQLRequest, HttpServletRequest request) {
        // 数据校验
        ThrowUtils.throwIf(chatForSQLRequest == null, ErrorCode.PARAMS_ERROR);
        String question = chatForSQLRequest.getQuestion();
        Long chatId = chatForSQLRequest.getChatId();
        ThrowUtils.throwIf(chatId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(question), ErrorCode.PARAMS_ERROR);
        // 校验提问字数 < 200字
        ThrowUtils.throwIf(question.length() > 200, ErrorCode.PARAMS_ERROR, "提问字数过多");
        // 限流
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        aiService.userChatForSQL(chatForSQLRequest, loginUser);
        return ResultUtils.success(true);
    }

    @Operation(summary = "查询用户选择对话的信息")
    @PostMapping("/get/chat")
    public BaseResponse<GetUserChatHistoryVO> getChatById(@RequestBody GetChatRequest getChatRequest, HttpServletRequest request) {
        // 数据校验
        ThrowUtils.throwIf(getChatRequest == null, ErrorCode.PARAMS_ERROR);
        Long chatId = getChatRequest.getChatId();
        ThrowUtils.throwIf(ObjectUtils.isEmpty(chatId), ErrorCode.PARAMS_ERROR);
        Chat chat = chatService.getById(chatId);
        // modelId查询对应的助手信息
        ThrowUtils.throwIf(chat == null, ErrorCode.PARAMS_ERROR);
        AiRole aiRole = aiRoleService.getById(chat.getModelId());
        GetUserChatHistoryVO getUserChatHistoryVO = new GetUserChatHistoryVO();
        if (aiRole == null) { // 是用户创建的助手
            UserCreateAssistant userCreateAssistant = userCreateAssistantService.getById(chat.getModelId());
            // 填充信息
            getUserChatHistoryVO.setChatId(chatId);
            getUserChatHistoryVO.setAssistantName(userCreateAssistant.getAssistantName());
            getUserChatHistoryVO.setFunctionDes(userCreateAssistant.getFunctionDes());
        }
        if (aiRole != null) {
            // 填充信息
            getUserChatHistoryVO.setChatId(chatId);
            getUserChatHistoryVO.setAssistantName(aiRole.getAssistantName());
            getUserChatHistoryVO.setFunctionDes(aiRole.getFunctionDes());
        }
        // 查询对应数据源信息 填充数据源信息
        DatasourceDTO dataSource = coreDatasourceService.getDataSource(chat.getDatasourceId(), userService.getLoginUser(request));
        getUserChatHistoryVO.setDatasourceId(chat.getDatasourceId());
        getUserChatHistoryVO.setDatasourceName(dataSource.getName());
        getUserChatHistoryVO.setDatasourceType(dataSource.getType());
        // 校验提问字数 < 200字
        return ResultUtils.success(getUserChatHistoryVO);
    }

    @Operation(summary = "查询用户是否添加了该聊天")
    @PostMapping("/chat/add")
    public BaseResponse<Long> userAddChat(@RequestBody UserAddChatRequest userAddChatRequest, HttpServletRequest request) {
        // 数据校验
        ThrowUtils.throwIf(userAddChatRequest == null, ErrorCode.PARAMS_ERROR);
        Long modelId = userAddChatRequest.getModelId();
        ThrowUtils.throwIf(ObjectUtils.isEmpty(modelId), ErrorCode.PARAMS_ERROR);
        // 限流
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<Chat> qw = new QueryWrapper<>();
        qw.eq("userId", loginUser.getId()).eq("modelId", modelId);
        Chat chat = chatService.getOne(qw);
        if (chat == null) { //不存在就创建，存在就返回
            chat = new Chat();
            chat.setUserId(loginUser.getId());
            chat.setModelId(modelId);
            boolean save = chatService.save(chat);
            ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(chat.getId());
    }

    @Operation(summary = "用户获取AI对话历史")
    @PostMapping("/get/chatRecord")
    public BaseResponse<List<ChatHistory>> getUserChatRecord(@RequestBody GetUserChatRecordRequest getUserChatRecordRequest, HttpServletRequest request) {
        // 数据校验
        ThrowUtils.throwIf(getUserChatRecordRequest == null, ErrorCode.PARAMS_ERROR);
        Long chatId = getUserChatRecordRequest.getChatId();
        ThrowUtils.throwIf(ObjectUtils.isEmpty(chatId), ErrorCode.PARAMS_ERROR);
        // 获取历史对话信息
        Chat chat = chatService.getById(chatId);
        // 获取历史对话，拆封数据
        ThrowUtils.throwIf(chat == null, ErrorCode.PARAMS_ERROR);
        List<ChatHistory> res = chatHistoryService.getUserChatRecord(chatId);
        return ResultUtils.success(res);
    }

    @Operation(summary = "用户获取AI对话历史")
    @PostMapping("/get/sql/chatRecord")
    public BaseResponse<List<GetUserSQLChatRecordVO>> getUserSQLChatRecord(@RequestBody GetUserChatRecordRequest getUserChatRecordRequest, HttpServletRequest request) {
        // 数据校验
        ThrowUtils.throwIf(getUserChatRecordRequest == null, ErrorCode.PARAMS_ERROR);
        Long chatId = getUserChatRecordRequest.getChatId();
        ThrowUtils.throwIf(ObjectUtils.isEmpty(chatId), ErrorCode.PARAMS_ERROR);
        // 获取历史对话信息
        Chat chat = chatService.getById(chatId);
        // 获取历史对话，拆封数据
        ThrowUtils.throwIf(chat == null, ErrorCode.PARAMS_ERROR);
        List<GetUserSQLChatRecordVO> res = chatHistoryService.getUserSQLChatRecord(chatId);
        return ResultUtils.success(res);
    }


    @Operation(summary = "用户创建AI对话")
    @PostMapping("/add/history")
    public BaseResponse<Boolean> addUserChatHistory(@RequestBody AddUserChatHistory addUserChatHistory, HttpServletRequest request) {
        // 数据校验
        ThrowUtils.throwIf(addUserChatHistory == null, ErrorCode.PARAMS_ERROR);
        Long modelId = addUserChatHistory.getModelId();
        ThrowUtils.throwIf(modelId == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Boolean res = chatService.addUserChatHistory(modelId, loginUser);
        return ResultUtils.success(res);
    }

    @Operation(summary = "智能问数单条对话分页查询")
    @GetMapping("/get/singleHistory/pageData/{chatHistoryId}/{pageNo}")
    public BaseResponse<CustomPage<Map<String, Object>>> getSingleHistoryPageData(@PathVariable Long chatHistoryId, @PathVariable Integer pageNo, HttpServletRequest request) {
        ThrowUtils.throwIf(chatHistoryId == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (pageNo == null) pageNo = 1;
        CustomPage<Map<String, Object>> res = chatService.getSingleHistoryPageData(chatHistoryId, pageNo, loginUser);
        return ResultUtils.success(res);
    }

    @Operation(summary = "用户创建智能问数对话")
    @PostMapping("/add/askSql/history")
    public BaseResponse<Boolean> addUserAskSqlHistory(@RequestBody AddUserAskSqlHistoryRequest addUserAskSqlHistory, HttpServletRequest request) {
        // 数据校验
        ThrowUtils.throwIf(addUserAskSqlHistory == null, ErrorCode.PARAMS_ERROR);
        Long dataId = addUserAskSqlHistory.getDataId();
        ThrowUtils.throwIf(dataId == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Boolean res = chatService.addUserAskSqlHistory(dataId, loginUser);
        return ResultUtils.success(res);
    }

    @Operation(summary = "删除用户智能问数对话")
    @DeleteMapping("/delete/askSql/history/{chatId}")
    public BaseResponse<Boolean> deleteUserAskSqlHistory(@PathVariable("chatId") Long chatId, HttpServletRequest request) {
        // 数据校验
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", chatId);
        List<Chat> deleteChatList = chatService.list(queryWrapper);

        if (deleteChatList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除数据不存在");
        }
        if (deleteChatList.size() > 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "存在多个同一chatId的记录");
        }
        Chat deleteChat = deleteChatList.get(0);
        User loginUser = userService.getLoginUser(request);
        if (!Objects.equals(deleteChat.getUserId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean remove = chatService.removeById(deleteChat);
        if (!remove) {
            log.error("数据库操作失败系统异常");
        }
        return ResultUtils.success(true);
    }

    @Operation(summary = "获取用户创建的AI对话")
    @GetMapping("/get/history")
    public BaseResponse<List<GetUserChatHistoryVO>> getUserChatHistory(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<GetUserChatHistoryVO> res = chatService.getUserChatHistory(loginUser);
        return ResultUtils.success(res);
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
            sb.append("你是").append(type).append("方面的专家").append(",");
        }
        if (StringUtils.isNotEmpty(roleDesign)) {
            sb.append("角色设定:").append(roleDesign).append(",");
        }
        if (StringUtils.isNotEmpty(targetWork)) {
            sb.append("你的任务是").append(targetWork).append(",");
        }
        if (StringUtils.isNotEmpty(functionDes)) {
            sb.append("功能描述:").append(functionDes).append(",");
        }
        if (StringUtils.isNotEmpty(inputModel)) {
            sb.append("输入模板:").append(inputModel).append(",");
        }
        if (StringUtils.isNotEmpty(requirement)) {
            sb.append("需求说明:").append(requirement).append(",");
        }
        if (StringUtils.isNotEmpty(style)) {
            sb.append("回答风格:").append(style).append(",");
        }
        if (StringUtils.isNotEmpty(otherRequire)) {
            sb.append("其它需求:").append(otherRequire).append(",");
        }
        if (StringUtils.isNotEmpty(text)) {
            sb.append("我要问的是:").append(text);
        }
        return sb.toString();
    }
}
