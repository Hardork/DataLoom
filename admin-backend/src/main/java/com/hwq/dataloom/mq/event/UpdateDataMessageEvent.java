package com.hwq.dataloom.mq.event;

import com.hwq.dataloom.constant.MqConstant;
import com.hwq.dataloom.framework.mq.model.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/15
 * @Description: 修改数据事件
 **/
@Component
public class UpdateDataMessageEvent extends BaseEvent<UpdateDataMessageEvent.UpdateDataMessage> {

    @Override
    public EventMessage<UpdateDataMessageEvent.UpdateDataMessage> buildEventMessage(UpdateDataMessageEvent.UpdateDataMessage data) {
        return EventMessage.<UpdateDataMessageEvent.UpdateDataMessage>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return MqConstant.UPDATE_DATA_QUEUE_NAME;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateDataMessage{
        /**
         * 修改者
         */
        private String updateUserName;
        /**
         * 修改内容
         */
        private String updateContent;

        /**
         * 修改时间
         */
        private Date updateDate;

        /**
         * 数据集id
         */
        private Long dataId;
        /**
         * 发送方式
         */
        private String type;
    }
}
