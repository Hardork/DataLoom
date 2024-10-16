package com.hwq.dataloom.product.mq.producer;

import cn.hutool.core.util.StrUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.product.constants.Constants;
import com.hwq.dataloom.product.mq.dto.BasicDTO;
import com.hwq.dataloom.product.mq.event.ProductDistributeEvent;
import com.hwq.dataloom.product.mq.wrapper.MessageWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/16
 * @Description:
 **/
@Component
public class ProductDistributeProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 构建请求类
     * @param productDistributeEvent
     * @return 请求类
     */
    public BasicDTO buildBaseSendDTO(ProductDistributeEvent productDistributeEvent) {
        String outBusinessNo = productDistributeEvent.getOutBusinessNo();
        return BasicDTO.builder()
                .eventName("商品发放")
                .key(outBusinessNo)
                .topic(Constants.MQKey.PRODUCT_DISTRIBUTE_TOPIC)
                .build();
    }

    /**
     * 构建payload
     * @param productDistributeEvent 延迟消息
     * @param basicDTO 请求类
     * @return payload
     */
    public Message<?> buildMessage(ProductDistributeEvent productDistributeEvent, BasicDTO basicDTO) {
        return MessageBuilder
                .withPayload(new MessageWrapper<>(basicDTO.getKey(), productDistributeEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, basicDTO.getKey())
                .setHeader(MessageConst.PROPERTY_TAGS, basicDTO.getTag())
                .build();

    }

    /**
     * 发送消息
     * @param productDistributeEvent 发送消息
     * @return 发送结果
     */
    public SendResult sendMessage(ProductDistributeEvent productDistributeEvent) {
        BasicDTO basicDTO = buildBaseSendDTO(productDistributeEvent);
        StringBuilder des = StrUtil.builder().append(basicDTO.getTopic());
        if (StringUtils.isNotEmpty(basicDTO.getTag())) {
            des.append(":").append(basicDTO.getTag());
        }
        // 判断类型
        if (basicDTO.getDelayTime() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "延迟时间不得为空");
        }
        return rocketMQTemplate.syncSendDelayTimeMills(String.valueOf(des), buildMessage(productDistributeEvent, basicDTO), basicDTO.getDelayTime());
    }
}
