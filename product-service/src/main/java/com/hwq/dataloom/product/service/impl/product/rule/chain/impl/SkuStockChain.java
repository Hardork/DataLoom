package com.hwq.dataloom.product.service.impl.product.rule.chain.impl;

import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.product.constants.Constants;
import com.hwq.dataloom.product.model.entity.Product;
import com.hwq.dataloom.product.redis.IRedisService;
import com.hwq.dataloom.product.service.impl.product.rule.chain.AbstractActionChain;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description:
 **/
@Slf4j
@Component("sku_stock_action")
public class SkuStockChain extends AbstractActionChain {

    @Resource
    private IRedisService redisService;
    @Override
    public boolean action(Product product) {
        Long productId = product.getId();
        // 扣减库存
        boolean status = subtractionSkuStock(productId);
        // true；库存扣减成功
        if (status) {
            // 写入延迟队列，延迟消费更新库存记录
            String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY + productId;
            RBlockingQueue<Long> blockingQueue = redisService.getBlockingQueue(cacheKey);
            RDelayedQueue<Long> delayedQueue = redisService.getDelayedQueue(blockingQueue);
            delayedQueue.offer(productId, 3, TimeUnit.SECONDS);
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    
    private boolean subtractionSkuStock(Long productId){
        long surplus = redisService.decr(Constants.RedisKey.SKU_STOCK + productId);
        if (surplus == 0) {
            // 库存消耗没了以后，发送MQ消息，更新数据库库存
        } else if (surplus < 0) {
            // 库存小于0，恢复为0个
            redisService.setValue(Constants.RedisKey.SKU_STOCK + productId,0);
            return false;
        }
        return true;
    }
}
