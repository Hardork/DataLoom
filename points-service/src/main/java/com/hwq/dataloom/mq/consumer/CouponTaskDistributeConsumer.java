package com.hwq.dataloom.mq.consumer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.hwq.dataloom.constants.CouponMessageConstant;
import com.hwq.dataloom.constants.CouponTaskConstant;
import com.hwq.dataloom.mapper.CouponTemplateMapper;
import com.hwq.dataloom.model.entity.CouponTask;
import com.hwq.dataloom.model.entity.CouponTaskFailRecord;
import com.hwq.dataloom.model.entity.CouponTemplate;
import com.hwq.dataloom.model.entity.UserCoupon;
import com.hwq.dataloom.model.enums.CouponTaskStatusEnum;
import com.hwq.dataloom.model.enums.UserCouponStatusEnum;
import com.hwq.dataloom.mq.event.CouponTaskDistributeEvent;
import com.hwq.dataloom.mq.wrapper.MessageWrapper;
import com.hwq.dataloom.service.CouponTaskFailRecordService;
import com.hwq.dataloom.service.CouponTaskService;
import com.hwq.dataloom.service.UserCouponService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.BatchExecutorException;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    private UserCouponService userCouponService;

    @Resource
    private CouponTaskFailRecordService couponTaskFailRecordService;

    @Resource
    private CouponTaskService couponTaskService;

    @Override
    public void onMessage(MessageWrapper<CouponTaskDistributeEvent> messageWrapper) {
        log.info("[消费者] 优惠券任务执行推送分发到用户账号（最终步骤） - 执行消费逻辑，消息体：{}", JSONUtil.toJsonStr(messageWrapper));

        // 当保存用户优惠券集合达到批量保存数量
        CouponTaskDistributeEvent message = messageWrapper.getMessage();
        if (!message.getDistributionEndFlag() && message.getBatchUserSetSize() % BATCH_USER_COUPON_SIZE == 0) {
            // 扣减库存并发放用户优惠券
            decrementCouponTemplateStockAndSaveUserCouponList(message);
        }
        // 分发任务结束标识为True时，代表Excel解析发放完毕
        if (message.getDistributionEndFlag()) {
            // 获取待发放用户集合
            String batchUserSetKey = String.format(CouponTaskConstant.COUPON_TASK_USER_SET_KEY, message.getCouponTaskId());
            Long batchUserIdsSize = stringRedisTemplate.opsForSet().size(batchUserSetKey);
            message.setBatchUserSetSize(batchUserIdsSize.intValue());
            decrementCouponTemplateStockAndSaveUserCouponList(message);
            // 确保目前没有待发放剩余用户
            List<String> batchUserMaps = stringRedisTemplate.opsForSet().pop(batchUserSetKey, Integer.MAX_VALUE);
            // 此时待保存入库用户优惠券列表如果还有值，就意味着可能库存不足引起的
            if (CollUtil.isNotEmpty(batchUserMaps)) {
                // 标记错误原因，方便后续查看未成功发送的原因和记录
                List<CouponTaskFailRecord> couponTaskFailDOList = new ArrayList<>(batchUserMaps.size());
                for (String batchUserMapStr : batchUserMaps) {
                    Map<Object, Object> objectMap = MapUtil.builder()
                            .put("rowNum", JSONUtil.parseObj(batchUserMapStr).getInt("rowNum"))
                            .put("cause", "库存不足无法发放")
                            .build();
                    CouponTaskFailRecord couponTaskFailDO = CouponTaskFailRecord.builder()
                            .batchId(message.getCouponTaskBatchId())
                            .failedContent(JSONUtil.toJsonStr(objectMap))
                            .build();
                    couponTaskFailDOList.add(couponTaskFailDO);
                }
                // 记录失败原因
                couponTaskFailRecordService.saveBatch(couponTaskFailDOList);
            }
            // 更新任务状态为成功
            CouponTask couponTask = CouponTask.builder()
                    .id(message.getCouponTaskId())
                    .completionTime(new Date())
                    .status(CouponTaskStatusEnum.SUCCEED.getStatus())
                    .build();
            couponTaskService.updateById(couponTask);
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
        List<String> readyToDistributeUserSet = stringRedisTemplate.opsForSet().pop(userSetKey, couponTemplateStock);
        List<UserCoupon> userCouponList = new ArrayList<>(message.getBatchUserSetSize());
        Date now = new Date();
        for (String userDistributeInfo : readyToDistributeUserSet) {
            JSONObject entries = JSONUtil.parseObj(userDistributeInfo);
            // 获取优惠券有效期
            DateTime validEndTime = DateUtil.offsetHour(now, JSONUtil.parseObj(message.getUsageRules()).getInt("validityPeriod"));
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId(entries.getLong("userId"))
                    .couponTemplateId(message.getCouponTemplateId())
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

    /**
     * 批量插入用户优惠券
     * @param couponTemplateId 优惠券模版id
     * @param couponTaskBatchId 优惠券任务批次id
     * @param userCouponList 待发放优惠券用户集合
     */
    private void batchSaveUserCouponList(Long couponTemplateId, Long couponTaskBatchId, List<UserCoupon> userCouponList) {
        try {
            userCouponService.saveBatch(userCouponList, userCouponList.size());
        } catch (Exception e) {
            Throwable cause = e.getCause();
            // 批量插入失败，主要的原因可能是
            //  1. 用户已经领了这个模版券，插入相同的模版券id时被唯一索引挡住了，是正常的，我们记录一下是重复领券的失败原因即可
            //  2. 其次还有可能是网络原因，这里我们暂不考虑
            if (cause instanceof BatchExecutorException) {
                // 查询出数据库哪些用户已经领了这个模版券，即为重复消费用户
                List<Long> userIds = userCouponList
                        .stream()
                        .map(UserCoupon::getUserId)
                        .collect(Collectors.toList());
                LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserCoupon::getCouponTemplateId, couponTemplateId)
                        .in(UserCoupon::getUserId, userIds);
                List<UserCoupon> repeatClaimUserList = userCouponService.list(wrapper);
                Set<Long> repeatUserIds =
                        repeatClaimUserList
                        .stream()
                        .map(UserCoupon::getUserId)
                        .collect(Collectors.toSet());
                // 将已经领完的用户从集合列表中移除
                userCouponList.removeIf(item -> repeatUserIds.contains(item.getUserId()));

                // 将重复领取的用户记录到数据库中
                List<CouponTaskFailRecord> failRecordList = new ArrayList<>(repeatClaimUserList.size());
                for (UserCoupon userCoupon : repeatClaimUserList) {
                    Map<Object, Object> reasonMap = MapUtil.builder()
                            .put("cause", "用户重复领取")
                            .build();
                    CouponTaskFailRecord couponTaskFailRecord = CouponTaskFailRecord.builder()
                            .batchId(couponTaskBatchId)
                            .userId(userCoupon.getUserId())
                            .failedContent(JSONUtil.toJsonStr(reasonMap))
                            .couponTemplateId(couponTemplateId)
                            .build();
                    failRecordList.add(couponTaskFailRecord);
                }
                couponTaskFailRecordService.saveBatch(failRecordList);
                // 重新对未领取用户集合进行优惠券发放
                if (!userCouponList.isEmpty()) {
                    batchSaveUserCouponList(couponTemplateId, couponTaskBatchId, userCouponList);
                }
            }
        }
    }

    /**
     * 减少优惠券库存
     * @param message 消息
     * @param batchUserSetSize 获取到优惠券的用户集合大小
     * @return 扣减成功数量
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
