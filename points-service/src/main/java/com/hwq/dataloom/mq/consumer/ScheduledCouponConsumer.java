package com.hwq.dataloom.mq.consumer;

import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.constants.CouponMessageConstant;
import com.hwq.dataloom.model.entity.CouponTask;
import com.hwq.dataloom.model.enums.CouponTaskStatusEnum;
import com.hwq.dataloom.mq.event.ScheduledCouponEvent;
import com.hwq.dataloom.mq.wrapper.MessageWrapper;
import com.hwq.dataloom.service.CouponTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/9/3 01:57
 * @description 定时优惠券发放消费者
 */
@Component
@RocketMQMessageListener(
        topic = CouponMessageConstant.SCHEDULE_COUPON_DISTRIBUTION_TOPIC,
        consumerGroup = CouponMessageConstant.SCHEDULE_COUPON_DISTRIBUTION_CONSUMER_GROUP
)
@Slf4j
public class ScheduledCouponConsumer implements RocketMQListener<MessageWrapper<ScheduledCouponEvent>> {

    @Resource
    private CouponTaskService couponTaskService;

    @Override
    public void onMessage(MessageWrapper<ScheduledCouponEvent> scheduledCouponEventMessageWrapper) {
        log.info("[消费者] 定时优惠券发放接收到消息 {}", JSONUtil.toJsonStr(scheduledCouponEventMessageWrapper));
        ScheduledCouponEvent message = scheduledCouponEventMessageWrapper.getMessage();
        Long couponTaskId = message.getCouponTaskId();
        CouponTask couponTask = couponTaskService.getById(couponTaskId);
        CouponTaskStatusEnum couponTaskStatusEnum = CouponTaskStatusEnum.findValueByType(couponTask.getStatus());
        if (couponTaskStatusEnum == CouponTaskStatusEnum.CANCELED) {
            log.error("任务ID: {} 已取消, 原因:{}", couponTaskId, "任务已被取消");
        }
        // 判断类型
        // TODO: 分批获取数据库中的所有用户进行优惠券发放
        // TODO：读取数据库中的缓存
    }
}
