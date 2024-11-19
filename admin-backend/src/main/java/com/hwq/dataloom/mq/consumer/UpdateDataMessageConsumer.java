package com.hwq.dataloom.mq.consumer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.hwq.dataloom.constant.MqConstant;
import com.hwq.dataloom.framework.mq.model.BaseEvent;
import com.hwq.dataloom.manager.message.update_data.IUpdateDataMessageService;
import com.hwq.dataloom.manager.model.UpdateDataBuildMessage;
import com.hwq.dataloom.model.vo.UserVO;
import com.hwq.dataloom.mq.event.UpdateDataMessageEvent;
import com.hwq.dataloom.service.UserDataPermissionService;
import com.hwq.dataloom.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/15
 * @Description:
 **/
@Slf4j
@Component
public class UpdateDataMessageConsumer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Map<String, IUpdateDataMessageService> sendMessageServiceMap;

    @Resource
    private UserDataPermissionService userDataPermissionService;

    @Resource
    private UserService userService;

    @Resource
    private UpdateDataMessageEvent updateDataMessageEvent;

    private final String type = "update_date_email";

    @RabbitListener(queuesToDeclare = @Queue(MqConstant.UPDATE_DATA_QUEUE_NAME), ackMode = "MANUAL")
    public void receiveMessage(String message) {
        BaseEvent.EventMessage<UpdateDataMessageEvent.UpdateDataMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<UpdateDataMessageEvent.UpdateDataMessage>>() {
        }.getType());
        // todo 从 eventMessage 中获取发送类型 扩展其他方式
        IUpdateDataMessageService updateDataMessageService = sendMessageServiceMap.get(type);
        // 1. 构建发送消息
        UpdateDataMessageEvent.UpdateDataMessage messageData = eventMessage.getData();
        String sendMessage = updateDataMessageService.buildMessage(UpdateDataBuildMessage.builder()
                .updateUserName(messageData.getUpdateUserName())
                .updateContent(messageData.getUpdateContent())
                .updateDate(messageData.getUpdateDate())
                .build());
        // 2. 获取发送的人员
        // 2.1 获取userId
        List<Long> userIdList = userDataPermissionService.queryUserIdByDataId(messageData.getDataId());
        // 2.2 根据id获取到对于人员的联系方式
        List<UserVO> userVOS = userService.getUserEmailById(userIdList);
        // 3. 发送消息
        userVOS.forEach(userVO -> {
            String email = userVO.getEmail();
            if (StringUtils.isBlank(email)) {
                log.warn("用户的邮箱不存在。userId：{} ", userVO.getId());
                return;
            }
            updateDataMessageService.send(sendMessage, email);
        });

    }
}
