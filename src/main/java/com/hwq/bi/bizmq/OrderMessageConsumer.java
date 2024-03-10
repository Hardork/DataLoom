package com.hwq.bi.bizmq;

import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.entity.ProductOrder;
import com.hwq.bi.model.enums.OrderStatusEnum;
import com.hwq.bi.model.enums.WebSocketMsgTypeEnum;
import com.hwq.bi.service.ProductOrderService;
import com.hwq.bi.websocket.UserWebSocket;
import com.hwq.bi.websocket.vo.WebSocketMsgVO;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private UserWebSocket userWebSocket;
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.ORDER_DEAD_QUEUE_NAME}, ackMode = "MANUAL")
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
