package com.hwq.dataloom.mq.consumer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.hwq.dataloom.constants.CouponMessageConstant;
import com.hwq.dataloom.model.entity.CouponTask;
import com.hwq.dataloom.model.entity.CouponTemplate;
import com.hwq.dataloom.model.enums.CouponStatusEnum;
import com.hwq.dataloom.model.enums.CouponTaskStatusEnum;
import com.hwq.dataloom.mq.event.CouponTaskDirectEvent;
import com.hwq.dataloom.mq.producer.CouponTaskDistributeMessageProducer;
import com.hwq.dataloom.mq.wrapper.MessageWrapper;
import com.hwq.dataloom.service.CouponTaskFailRecordService;
import com.hwq.dataloom.service.CouponTaskService;
import com.hwq.dataloom.service.CouponTemplateService;
import com.hwq.dataloom.service.excel.CouponTaskExcelEntity;
import com.hwq.dataloom.service.excel.CouponTaskExcelListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/9/1 01:05
 * @description 优惠券发放消费者
 * 解析excel文件
 */
@Component
@RocketMQMessageListener(
        topic = CouponMessageConstant.EXCEL_ANALYSIS_TOPIC,
        consumerGroup = CouponMessageConstant.EXCEL_ANALYSIS_CONSUMER_GROUP
)
@Slf4j
public class CouponTaskDirectConsumer implements RocketMQListener<MessageWrapper<CouponTaskDirectEvent>> {

    @Resource
    private CouponTaskService couponTaskService;

    @Resource
    private CouponTemplateService couponTemplateService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CouponTaskFailRecordService couponTaskFailRecordService;

    @Resource
    private CouponTaskDistributeMessageProducer couponTaskDistributeMessageProducer;


    @Override
    public void onMessage(MessageWrapper<CouponTaskDirectEvent> messageWrapper) {
        log.info("优惠券发放消费者接收到消息，消息内容{}", JSON.toJSONString(messageWrapper));
        // 1. 查询优惠券状态
        // 2. 读取excel文件，并进行用户优惠券发放
        // 3. 对缓存中优惠券模版库存进行扣减
        // 4. 库存为0，记录失败原因，结束任务
        // 5. 如果
        CouponTaskDirectEvent event = messageWrapper.getMessage();
        Long couponTaskId = event.getCouponTaskId();
        CouponTask couponTask = couponTaskService.getById(couponTaskId);
        if (couponTask == null) {
            log.error("任务不存在, id:{}", couponTaskId);
            return;
        }
        CouponTaskStatusEnum couponTaskStatusEnum = CouponTaskStatusEnum.findValueByType(couponTask.getStatus());
        if (couponTaskStatusEnum == CouponTaskStatusEnum.CANCELED) { // 任务已取消
            log.info("优惠券发放任务状态异常，已取消执行优惠券发放, couponTaskId:{}", couponTaskId);
            return;
        }
        Long couponTemplateId = couponTask.getCouponTemplateId();
        CouponTemplate couponTemplate = couponTemplateService.getById(couponTemplateId);
        CouponStatusEnum couponStatusEnum = CouponStatusEnum.findValueByType(couponTemplate.getStatus());
        if (couponStatusEnum == CouponStatusEnum.OFFLINE) {
            log.error("优惠券发放失败: 优惠券模版ID: {}，失败原因：对应优惠券模版已下线", couponTemplateId);
        }
        // 开始执行优惠券发放
        CouponTaskExcelListener couponTaskExcelListener = new CouponTaskExcelListener(
                couponTask,
                stringRedisTemplate,
                couponTaskFailRecordService,
                couponTemplate,
                couponTaskDistributeMessageProducer
        );
        EasyExcel.read(couponTask.getUserListFilePath(), CouponTaskExcelEntity.class, couponTaskExcelListener).sheet().doRead();
    }
}
