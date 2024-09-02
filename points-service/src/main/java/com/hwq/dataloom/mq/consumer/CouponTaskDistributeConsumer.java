package com.hwq.dataloom.mq.consumer;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.hwq.dataloom.constants.CouponMessageConstant;
import com.hwq.dataloom.constants.CouponTaskConstant;
import com.hwq.dataloom.mapper.CouponTemplateMapper;
import com.hwq.dataloom.mapper.UserCouponMapper;
import com.hwq.dataloom.model.entity.CouponTemplate;
import com.hwq.dataloom.model.entity.UserCoupon;
import com.hwq.dataloom.model.enums.UserCouponStatusEnum;
import com.hwq.dataloom.mq.event.CouponTaskDistributeEvent;
import com.hwq.dataloom.mq.wrapper.MessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author HWQ
 * @date 2024/9/1 23:33
 * @description 优惠券发放消费者
 */
@RocketMQMessageListener(
        topic = CouponMessageConstant.COUPON_DISTRIBUTION_TOPIC,
        consumerGroup = CouponMessageConstant.COUPON_DISTRIBUTION_CONSUMER_GROUP
)
@Slf4j
public class CouponTaskDistributeConsumer implements RocketMQListener<MessageWrapper<CouponTaskDistributeEvent>> {

    private final static int BATCH_USER_COUPON_SIZE = 5000;

    @Resource
    private CouponTemplateMapper couponTemplateMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserCouponMapper userCouponMapper;

    @Override
    public void onMessage(MessageWrapper<CouponTaskDistributeEvent> messageWrapper) {
        log.info("[消费者] 优惠券任务执行推送@分发到用户账号 - 执行消费逻辑，消息体：{}", JSONUtil.toJsonStr(messageWrapper));

        // 当保存用户优惠券集合达到批量保存数量
        CouponTaskDistributeEvent message = messageWrapper.getMessage();
        if (!message.getDistributionEndFlag() && message.getBatchUserSetSize() % BATCH_USER_COUPON_SIZE == 0) {
            decrementCouponTemplateStockAndSaveUserCouponList(message);
        }
    }

    /**
     * 扣减优惠券库存并且保存发券记录
     * @param message 消息
     */
    private void decrementCouponTemplateStockAndSaveUserCouponList(CouponTaskDistributeEvent message) {
        // 如果等于 0 意味着已经没有了库存，直接返回即可
        Integer couponTemplateStock = decrementCouponTemplateStock(message, message.getBatchUserSetSize());
        if (couponTemplateStock <= 0) { // 所有的优惠券库存都用完了
            return;
        }
        // couponTemplateStock为扣减成功的库存，发放couponTemplateStock的数量给用户
        String userSetKey = String.format(CouponTaskConstant.COUPON_TASK_USER_SET_KEY, message.getCouponTaskId());
        // 从缓存中获取待发放优惠券的用户集合
        List<String> readyToDistributeUserSet = stringRedisTemplate.opsForList().leftPop(userSetKey, couponTemplateStock);
        List<UserCoupon> userCouponList = new ArrayList<>(message.getBatchUserSetSize());
        Date now = new Date();
        for (String userDistributeInfo : readyToDistributeUserSet) {
            JSONObject entries = JSONUtil.parseObj(userDistributeInfo);
            // 获取优惠券有效期
            DateTime validEndTime = DateUtil.offsetHour(now, JSONUtil.parseObj(message.getUsageRules()).getInt("validityPeriod"));
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId(entries.getLong("userId"))
                    .receiveCount(1)
                    .source(1) // 平台券
                    .status(UserCouponStatusEnum.UNUSED.getStatus())
                    .receiveTime(now)
                    .validStartTime(validEndTime)
                    .build();
            userCouponList.add(userCoupon);
        }

        // 批量发放优惠券
        batchSaveUserCouponList(message.getCouponTemplateId(), message.getCouponTaskBatchId(), userCouponList);
    }

    private void batchSaveUserCouponList(Long couponTemplateId, Long couponTaskBatchId, List<UserCoupon> userCouponList) {
        // TODO: 批量插入用户优惠券
    }

    /**
     * 减少优惠券库存
     * @param message 消息
     * @param batchUserSetSize 获取到优惠券的用户集合大小
     * @return
     */
    private Integer decrementCouponTemplateStock(CouponTaskDistributeEvent message, Integer batchUserSetSize) {
        // 通过乐观锁机制自减优惠券库存记录
        Long couponTemplateId = message.getCouponTemplateId();
        // 尝试扣减库存
        int successNum = couponTemplateMapper.decrementCouponTemplateStock(couponTemplateId, batchUserSetSize);
        // 扣减失败
        if (!SqlHelper.retBool(successNum)) {
            // 查询剩余库存
            LambdaQueryWrapper<CouponTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CouponTemplate::getId, couponTemplateId);
            CouponTemplate couponTemplate = couponTemplateMapper.selectOne(wrapper);
            // 自旋转扣减库存
            return decrementCouponTemplateStock(message, couponTemplate.getStock());
        }
        return successNum;
    }
}
