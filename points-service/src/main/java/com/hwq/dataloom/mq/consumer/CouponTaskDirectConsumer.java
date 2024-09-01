package com.hwq.dataloom.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.hwq.dataloom.constants.CouponMessageConstant;
import com.hwq.dataloom.model.entity.CouponTask;
import com.hwq.dataloom.model.entity.CouponTemplate;
import com.hwq.dataloom.model.enums.CouponStatusEnum;
import com.hwq.dataloom.mq.event.CouponTemplateDirectEvent;
import com.hwq.dataloom.mq.wrapper.MessageWrapper;
import com.hwq.dataloom.service.CouponTaskService;
import com.hwq.dataloom.service.CouponTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/9/1 01:05
 * @description 优惠券发放消费者
 * 将优惠券发放到用户券包中
 */
@Component
@RocketMQMessageListener(
        topic = CouponMessageConstant.DIRECT_MESSAGE_TOPIC,
        consumerGroup = CouponMessageConstant.DIRECT_MESSAGE_CONSUMER_GROUP
)
@Slf4j
public class CouponTaskDirectConsumer implements RocketMQListener<MessageWrapper<CouponTemplateDirectEvent>> {

    @Resource
    private CouponTaskService couponTaskService;

    @Resource
    private CouponTemplateService couponTemplateService;


    @Override
    public void onMessage(MessageWrapper<CouponTemplateDirectEvent> messageWrapper) {
        log.info("优惠券发放消费者接收到消息，消息内容{}", JSON.toJSONString(messageWrapper));
        // 1. 查询优惠券状态
        // 2. 读取excel文件，并进行用户优惠券发放
        // 3. 对缓存中优惠券模版库存进行扣减
        // 4. 库存为0，记录失败原因，结束任务
        // 5. 如果
        Long couponTemplateId = messageWrapper.getMessage().getCouponTemplateId();
        CouponTemplate couponTemplate = couponTemplateService.getById(couponTemplateId);
        CouponStatusEnum couponStatusEnum = CouponStatusEnum.findValueByType(couponTemplate.getStatus());
        if (couponStatusEnum == CouponStatusEnum.OFFLINE) {
            log.error("优惠券发放失败: 优惠券模版ID: {}，失败原因：对应优惠券模版已下线", couponTemplateId);
        }
        // 开始执行优惠券发放

    }
}
