package com.hwq.dataloom.service.excel;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.entity.CouponTask;
import com.hwq.dataloom.model.entity.CouponTaskFailRecord;
import com.hwq.dataloom.model.entity.CouponTemplate;
import com.hwq.dataloom.mq.event.CouponTaskDistributeEvent;
import com.hwq.dataloom.mq.producer.CouponTaskDistributeMessageProducer;
import com.hwq.dataloom.service.CouponTaskFailRecordService;
import com.hwq.dataloom.utils.StockDecrementReturnCombinedUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Map;

import static com.hwq.dataloom.constants.CouponConstant.COUPON_TEMPLATE_INFO_KEY;
import static com.hwq.dataloom.constants.CouponTaskConstant.COUPON_TASK_PROCESS_KEY;
import static com.hwq.dataloom.constants.CouponTaskConstant.COUPON_TASK_USER_SET_KEY;

/**
 * @author HWQ
 * @date 2024/9/1 16:58
 * @description 解析优惠券发放的excel，将解析出的用户对象推送到MQ中进行消费
 */
@RequiredArgsConstructor
public class CouponTaskExcelListener extends AnalysisEventListener<CouponTaskExcelEntity> {

    private final CouponTask couponTask;

    private final StringRedisTemplate stringRedisTemplate;

    private final CouponTaskFailRecordService couponTaskFailRecordService;

    private final CouponTemplate couponTemplate;

    private final CouponTaskDistributeMessageProducer couponTaskDistributeMessageProducer;

    private final static String STOCK_DECREMENT_AND_BATCH_SAVE_USER_RECORD_LUA_PATH = "lua/stock_decrement_and_batch_save_user_record.lua";

    private final static int MAX_BATCH_COUPON_SIZE = 5000;
    /**
     * 记录处理的数据量
     */
    @Getter
    private int rowNum = 1;

    /**
     * 每读取到一行记录都会触发这个函数
     * 这里对应的
     * @param couponTaskExcelEntity 行记录对应实体类
     * @param analysisContext 上下文对象
     */
    @Override
    public void invoke(CouponTaskExcelEntity couponTaskExcelEntity, AnalysisContext analysisContext) {
        // 获取任务ID
        Long couponTaskId = couponTask.getId();

        // 获取当前任务执行进度条, 判断当前记录行是否已经执行过了。如果执行过，直接跳过
        String processKey = String.format(COUPON_TASK_PROCESS_KEY, couponTaskId);
        String curProcess = stringRedisTemplate.opsForValue().get(processKey);
        if (StringUtils.isNotEmpty(curProcess) && Integer.parseInt(curProcess) >= rowNum) {
            ++rowNum;
            return;
        }
        // 获取LUA脚本, 扣减缓存中的优惠券库存，返回成功扣减库存的数量
        DefaultRedisScript<Long> decrementLuaScript = Singleton.get(STOCK_DECREMENT_AND_BATCH_SAVE_USER_RECORD_LUA_PATH, () -> {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(STOCK_DECREMENT_AND_BATCH_SAVE_USER_RECORD_LUA_PATH)));
            redisScript.setResultType(Long.class);
            return redisScript;
        });

        // 设置Lua脚本参数
        String couponTemplateKey = String.format(COUPON_TEMPLATE_INFO_KEY, couponTask.getCouponTemplateId());
        String couponTaskUserSetKey = String.format(COUPON_TASK_USER_SET_KEY, couponTaskId);
        Map<Object, Object> args = MapUtil.builder()
                .put("rowNum", rowNum + 1)
                .put("userId", couponTaskExcelEntity.getUserId())
                .build();
        Long combinedField = stringRedisTemplate.execute(decrementLuaScript, ListUtil.toList(couponTemplateKey, couponTaskUserSetKey), JSONUtil.toJsonStr(args));
        boolean firstField = StockDecrementReturnCombinedUtil.extractFirstField(combinedField);
        // 存在扣减失败, 记录失败原因，直接返回
        if (!firstField) {
            // 同步当前执行进度到缓存
            stringRedisTemplate.opsForValue().set(processKey, String.valueOf(rowNum));
            // 存储失败原因
            Map<Object, Object> failReason = MapUtil.builder()
                    .put("rowNum", rowNum + 1)
                    .put("cause", "对应优惠券模版无库存")
                    .build();
            CouponTaskFailRecord couponTaskFailRecord = CouponTaskFailRecord.builder()
                    .couponTemplateId(couponTask.getCouponTemplateId())
                    .batchId(couponTask.getBatchId())
                    .failedContent(JSONUtil.toJsonStr(failReason))
                    .build();
            ThrowUtils.throwIf(!couponTaskFailRecordService.save(couponTaskFailRecord), ErrorCode.SYSTEM_ERROR, "数据库异常");
            return;
        }

        // 获取用户领券集合长度
        int batchUserSetSize = StockDecrementReturnCombinedUtil.extractSecondField(combinedField.intValue());
        // 如果没有消息通知需求，仅在 batchUserSetSize == MAX_BATCH_COUPON_SIZE 时发送消息消费。不满足条件仅记录执行进度即可
        if (batchUserSetSize < MAX_BATCH_COUPON_SIZE && StringUtils.isBlank(couponTask.getNotifyType())) {
            // 同步当前excel进度到缓存
            stringRedisTemplate.opsForValue().set(processKey, String.valueOf(rowNum));
            ++rowNum;
            return;
        }
        // 当前用户需要消息通知或者到达批次数量
        // 发送到消息队列中进行优惠券发放以及消息通知
        CouponTaskDistributeEvent couponTaskDistributeEvent = CouponTaskDistributeEvent.builder()
                .couponTaskBatchId(couponTaskId)
                .couponTaskId(couponTaskId)
                .batchUserSetSize(batchUserSetSize)
                .mail(couponTaskExcelEntity.getEmail())
                .phone(couponTaskExcelEntity.getPhone())
                .notifyType(couponTask.getNotifyType())
                .userId(couponTaskExcelEntity.getUserId())
                .distributionEndFlag(Boolean.FALSE)
                .usageRules(couponTemplate.getUsageRules())
                .build();

        couponTaskDistributeMessageProducer.sendMessage(couponTaskDistributeEvent);
        // 更加进度条
        stringRedisTemplate.opsForValue().set(processKey, String.valueOf(rowNum));
        rowNum++;
    }

    /**
     * 在读取excel完毕后触发，即使不满足批量保存的数量也得保存到数据库
     * @param analysisContext 上下文对象
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        CouponTaskDistributeEvent couponTaskDistributeEvent = CouponTaskDistributeEvent.builder()
                .distributionEndFlag(Boolean.TRUE)
                .couponTemplateId(couponTask.getCouponTemplateId())
                .usageRules(couponTemplate.getUsageRules())
                .couponTaskId(couponTask.getId())
                .build();
        couponTaskDistributeMessageProducer.sendMessage(couponTaskDistributeEvent);
    }
}
