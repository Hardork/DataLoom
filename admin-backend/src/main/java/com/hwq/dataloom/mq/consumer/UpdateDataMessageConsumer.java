package com.hwq.dataloom.mq.consumer;

import com.hwq.dataloom.manager.message.update_data.IUpdateDataMessage;
import com.hwq.dataloom.service.UserDataPermissionService;
import com.hwq.dataloom.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    private Map<String, IUpdateDataMessage> sendMessageMap;

    @Resource
    private UserDataPermissionService userDataPermissionService;

    @Resource
    private UserService userService;

    private final String type = "update_date_email";
//
//    @RabbitListener(queues = {MqConstant.UPDATE_DATA_QUEUE_NAME}, ackMode = "MANUAL")
//    public void receiveMessage(String message) {
//        UpdateDataMessageEntity updateDataMessageEntity = JSONUtil.toBean(message, UpdateDataMessageEntity.class);
//        // todo 扩展其他方式
//        IUpdateDataMessage updateDataMessage = sendMessageMap.get(type);
//        // 1. 构建发送消息
//        String sendMessage = updateDataMessage.buildMessage(UpdateDataBuildMessage.builder()
//                .updateUserName(updateDataMessageEntity.getUpdateUserName())
//                .updateContent(updateDataMessageEntity.getUpdateContent())
//                .updateDate(updateDataMessageEntity.getUpdateDate())
//                .build());
//        // 2. 获取发送的人员
//        // 2.1 获取userId
//        List<Long> userIdList = userDataPermissionService.queryUserIdByDataId(updateDataMessageEntity.getDataId());
//        // 2.2 根据id获取到对于人员的联系方式
//        List<UserVO> userVOS = userService.getUserEmailById(userIdList);
//        // 3. 发送消息
//        userVOS.forEach(userVO -> {
//            String email = userVO.getEmail();
//            if(StringUtils.isBlank(email)) {
//                log.warn("用户的邮箱不存在。userId：{} ",userVO.getId());
//                return;
//            }
//            updateDataMessage.send(sendMessage, email);
//        });
//
//    }
}
