package com.hwq.dataloom.mq.consumer;

import com.hwq.dataloom.constants.MqConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.enums.WebSocketMsgTypeEnum;
import com.hwq.dataloom.framework.service.InnerWSServiceInterface;
import com.hwq.dataloom.framework.ws.vo.WebSocketMsgVO;
import com.hwq.dataloom.model.entity.ProductOrder;
import com.hwq.dataloom.model.enums.OrderStatusEnum;
import com.hwq.dataloom.service.ProductOrderService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/3/10 17:46
 * @description 订单消费者
 */
@Component
@Slf4j
public class OrderMessageConsumer {

    @Resource
    private ProductOrderService productOrderService;

    @DubboReference
    private InnerWSServiceInterface userWebSocket;

    @SneakyThrows
    @RabbitListener(queues = {MqConstant.ORDER_DEAD_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveOrderMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StringUtils.isBlank(message)) {
            // 如果失败，消息拒绝
            channel.basicAck(deliveryTag, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long orderId = Long.parseLong(message);
        ProductOrder order = productOrderService.getById(orderId);
        if (order == null) {
            channel.basicAck(deliveryTag, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }
        // 更新订单信息为超时（前提是订单的状态为未支付）
        updateOrderTimeout(order);
        // 通知用户订单超时
        log.error("用户订单超时");
        notifyUserTimeout(order);
        // 消息确认
        channel.basicAck(deliveryTag, false);
    }

    private void updateOrderTimeout(ProductOrder order) {
        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getEnumByValue(order.getStatus());
        ThrowUtils.throwIf(orderStatusEnum == null, ErrorCode.SYSTEM_ERROR);
        if (orderStatusEnum.equals(OrderStatusEnum.NOT_PAY)) { // 改为超时
            order.setStatus(OrderStatusEnum.TIMEOUT.getValue());
            boolean update = productOrderService.updateById(order);
            ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        }
    }

    private void notifyUserTimeout(ProductOrder order) {
        WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
        webSocketMsgVO.setType(WebSocketMsgTypeEnum.ERROR.getValue());
        webSocketMsgVO.setTitle("订单超时");
        webSocketMsgVO.setDescription("失败原因：超时未支付");
        userWebSocket.sendOneMessage(order.getUserId(), webSocketMsgVO);
    }
}
