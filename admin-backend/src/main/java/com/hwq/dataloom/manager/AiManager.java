package com.hwq.dataloom.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.enums.RoleEnum;
import com.hwq.dataloom.utils.Message;
import com.hwq.dataloom.utils.MoonshotAiClient;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.hwq.dataloom.constant.PromptConstants.AI_GEN_CHART;

/**
 * 用于对接 AI 平台
 */
@Service
public class AiManager {

    @Resource
    private MoonshotAiClient moonshotAiClient;


    public String doChatWithKimi32K(String question, String prompt) {
        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), prompt),
                new Message(RoleEnum.user.name(), question)
        );
        return moonshotAiClient.chat("moonshot-v1-32k",messages);
    }

    public String doChatWithKimi128K(String question, String prompt) {
        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), prompt),
                new Message(RoleEnum.user.name(), question)
        );
        return moonshotAiClient.chat("moonshot-v1-128k",messages);
    }

    public String doChatWithKimi32KFlux(String question, String prompt, User loginUser) {
        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), prompt),
                new Message(RoleEnum.user.name(), question)
        );
        return moonshotAiClient.chatFlux("moonshot-v1-32k",messages, loginUser);
    }

    public String doChatWithKimi128KFlux(String question, String prompt, User loginUser) {
        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), prompt),
                new Message(RoleEnum.user.name(), question)
        );
        return moonshotAiClient.chatFlux("moonshot-v1-128k",messages, loginUser);
    }


}
