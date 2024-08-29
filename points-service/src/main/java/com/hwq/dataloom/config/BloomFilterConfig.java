package com.hwq.dataloom.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import static com.hwq.dataloom.constants.CouponConstant.COUPON_BLOOM_FILTER_KEY;

/**
 * @author HWQ
 * @date 2024/8/29 12:02
 * @description
 */
@Configuration
public class BloomFilterConfig {


    /**
     * 防止查询优惠券缓存穿透的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> CouponTemplateBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(COUPON_BLOOM_FILTER_KEY);
        bloomFilter.tryInit(3000, 0.02D);
        return bloomFilter;
    }
}
