package com.hwq.dataloom.service.impl;
import java.util.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.request.PageRequest;
import com.hwq.dataloom.model.dto.coupon.CouponTemplateNumberReqDTO;
import com.hwq.dataloom.model.dto.coupon.CouponTemplatePageQueryReqDTO;
import com.hwq.dataloom.model.dto.coupon.CouponTemplateSaveReqDTO;
import com.hwq.dataloom.model.entity.CouponTemplate;
import com.hwq.dataloom.model.enums.CouponStatusEnum;
import com.hwq.dataloom.model.vo.coupon.CouponTemplatePageQueryVO;
import com.hwq.dataloom.model.vo.coupon.CouponTemplateQueryVO;
import com.hwq.dataloom.mq.event.CouponTemplateDelayEvent;
import com.hwq.dataloom.mq.producer.CouponDelayMessageProducer;
import com.hwq.dataloom.service.CouponTemplateService;
import com.hwq.dataloom.mapper.CouponTemplateMapper;
import com.hwq.dataloom.service.basic.chain.CouponAbstractChainHandler;
import com.hwq.dataloom.service.basic.chain.CouponChainContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.stream.Collectors;

import static com.hwq.dataloom.constants.CouponConstant.COUPON_TEMPLATE_INFO_KEY;
import static com.hwq.dataloom.constants.CouponConstant.CREATE_COUPON_TEMPLATE_MASK;

/**
* @author wqh
* @description 针对表【coupon_template】的数据库操作Service实现
* @createDate 2024-08-27 13:03:15
*/
@Service
public class CouponTemplateServiceImpl extends ServiceImpl<CouponTemplateMapper, CouponTemplate>
    implements CouponTemplateService{

    @Resource
    private CouponChainContext couponChainContext;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CouponTemplateMapper couponTemplateMapper;


    @Resource
    private CouponDelayMessageProducer couponDelayMessageProducer;

    @Override
    public void createCouponTemplate(CouponTemplateSaveReqDTO requestParam) {
        // TODO：使用责任链串联校验
        couponChainContext.handle(CREATE_COUPON_TEMPLATE_MASK, requestParam);

        // 新增优惠券模版信息到数据库
        CouponTemplate couponTemplate = new CouponTemplate();
        BeanUtils.copyProperties(requestParam, couponTemplate);
        ThrowUtils.throwIf(!this.save(couponTemplate), ErrorCode.SYSTEM_ERROR);

        // 缓存预热：通过将数据库的记录序列化成JSON字符串放入Redis缓存
        CouponTemplateQueryVO couponTemplateQueryVO = new CouponTemplateQueryVO();
        BeanUtils.copyProperties(requestParam, couponTemplateQueryVO);

        // 将优惠券模版信息，利用hash缓存起来
        // explain: 在秒杀优惠券的时候，要确保优惠券的stock是原子性的操作，直接用String类型存储整个对象不行
        Map<String, Object> map = BeanUtil.beanToMap(couponTemplateQueryVO);
        Map<String, String> couponTemplateMap = map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() == null ? "" : entry.getValue().toString()));
        String couponTemplateKey = String.format(COUPON_TEMPLATE_INFO_KEY, couponTemplate.getId());

        // 通过 LUA 脚本执行设置 Hash 数据以及设置过期时间，避免内存泄漏
        // 废弃stringRedisTemplate.opsForHash().putAll(couponTemplateKey, couponTemplateMap);
        // 原因：putAll操作不能设置超时时间，而一个一个put存在原子性问题
        String luaScript = "redis.call('HMSET', KEYS[1], unpack(ARGV, 1, #ARGV - 1)) " +
                "redis.call('EXPIREAT', KEYS[1], ARGV[#ARGV])";

        List<String> keys = Collections.singletonList(couponTemplateKey);
        List<String> args = new ArrayList<>();
        couponTemplateMap.forEach((key, value) -> {
            args.add(key);
            args.add(value);
        });
        // 优惠券活动过期时间转换为秒级别的 Unix 时间戳
        args.add(String.valueOf(couponTemplate.getValidEndTime().getTime() / 1000));

        // 执行 LUA 脚本
        stringRedisTemplate.execute(
                new DefaultRedisScript<>(luaScript, Long.class),
                keys,
                args.toArray()
        );

        // 发送延时消息事件，优惠券活动到期修改优惠券模板状态
        CouponTemplateDelayEvent templateDelayEvent = CouponTemplateDelayEvent.builder()
                .couponTemplateId(couponTemplate.getId())
                .delayTime(couponTemplate.getValidEndTime().getTime())
                .build();
        couponDelayMessageProducer.sendMessage(templateDelayEvent);
    }

    @Override
    public Page<CouponTemplate> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam) {
        String name = requestParam.getName();
        Integer type = requestParam.getType();
        Integer status = requestParam.getStatus();
        long current = requestParam.getCurrent();
        long pageSize = requestParam.getPageSize();
        LambdaQueryWrapper<CouponTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .like(CouponTemplate::getName, name)
                .eq(Objects.nonNull(type), CouponTemplate::getType, type)
                .eq(Objects.nonNull(status), CouponTemplate::getStatus, status)
        ;
        return this.page(new Page<>(current, pageSize));
    }

    @Override
    public CouponTemplateQueryVO findCouponTemplateById(Long couponTemplateId) {
        // TODO：布隆过滤器，过滤恶意请求
        CouponTemplate couponTemplate = this.getById(couponTemplateId);
        CouponTemplateQueryVO couponTemplateQueryVO = new CouponTemplateQueryVO();
        BeanUtils.copyProperties(couponTemplate, couponTemplateQueryVO);
        return couponTemplateQueryVO;
    }

    @Override
    public void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam) {
        // 增加优惠券的发行量
        ThrowUtils.throwIf(requestParam.getNumber() < 0, ErrorCode.PARAMS_ERROR, "优惠券的发行量不得为负数");

        CouponTemplate couponTemplate = this.getById(requestParam.getCouponTemplateId());
        ThrowUtils.throwIf(couponTemplate == null, ErrorCode.NOT_FOUND_ERROR);

        // 验证优惠 券模板是否正常
        if (ObjectUtil.notEqual(couponTemplate.getStatus(), CouponStatusEnum.ONLINE.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "优惠券模板已结束");
        }

        // 更新优惠券数
        int row = couponTemplateMapper.increaseNumberCouponTemplate(requestParam);

        if (!SqlHelper.retBool(row)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "新增优惠券失败");
        }

        // 增加优惠券缓存
        String couponCacheKey = String.format(COUPON_TEMPLATE_INFO_KEY, couponTemplate.getId());
        stringRedisTemplate.opsForHash().increment(couponCacheKey, "stock", requestParam.getNumber());
    }

    @Override
    public void terminateCouponTemplate(Long couponTemplateId) {
        CouponTemplate couponTemplate = this.getById(couponTemplateId);

        // 验证优惠券模板状态是否正常
        if (ObjectUtil.notEqual(couponTemplate.getStatus(), CouponStatusEnum.ONLINE.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券模版已下线");
        }

        // 更新数据库
        couponTemplate.setStatus(CouponStatusEnum.OFFLINE.getStatus());
        ThrowUtils.throwIf(!this.updateById(couponTemplate), ErrorCode.SYSTEM_ERROR);

        // 更新缓存
        String couponCacheKey = String.format(COUPON_TEMPLATE_INFO_KEY, couponTemplate.getId());
        stringRedisTemplate.opsForHash().put(couponCacheKey, "status", String.valueOf(CouponStatusEnum.OFFLINE.getStatus()));
    }
}




