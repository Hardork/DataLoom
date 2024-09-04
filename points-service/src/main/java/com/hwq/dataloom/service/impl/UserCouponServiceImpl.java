package com.hwq.dataloom.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.hwq.dataloom.config.UserContext;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.mapper.CouponTemplateMapper;
import com.hwq.dataloom.model.dto.user_coupon.UserClaimCouponDTO;
import com.hwq.dataloom.model.entity.UserCoupon;
import com.hwq.dataloom.model.enums.CouponStatusEnum;
import com.hwq.dataloom.model.enums.RedisUserClaimCouponStatusEnum;
import com.hwq.dataloom.model.vo.coupon.CouponTemplateQueryVO;
import com.hwq.dataloom.service.CouponTemplateService;
import com.hwq.dataloom.service.UserCouponService;
import com.hwq.dataloom.mapper.UserCouponMapper;
import com.hwq.dataloom.utils.StockDecrementReturnCombinedUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

import java.util.Date;

import static com.hwq.dataloom.constants.CouponConstant.COUPON_TEMPLATE_INFO_KEY;
import static com.hwq.dataloom.constants.UserCouponConstant.USER_CLAIM_COUPON_COUNT;

/**
* @author wqh
* @description 针对表【user_coupon(用户优惠券表)】的数据库操作Service实现
* @createDate 2024-09-02 02:48:42
*/
@Service
@Slf4j
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon>
    implements UserCouponService{

    @Resource
    private CouponTemplateService couponTemplateService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserCouponService userCouponService;

    @Resource
    private CouponTemplateMapper couponTemplateMapper;

    @Resource
    private TransactionTemplate transactionTemplate;


    private final static String STOCK_DECREMENT_AND_SAVE_USER_RECORD_LUA_PATH = "lua/stock_decrement_and_save_user_record.lua";


    @Override
    public void userClaimCoupon(UserClaimCouponDTO requestParam) {
        Long userId = UserContext.getUserId();
        Long couponTemplateId = requestParam.getCouponTemplateId();
        // 1.从缓存中查询出优惠券信息
        CouponTemplateQueryVO couponTemplate = couponTemplateService.findCouponTemplateById(couponTemplateId);

        // 2.使用Lua脚本进行以下操作
        // 2.1 查询对应的库存是否 > 0 ，库存不足直接返回
        // 2.2 查询对应的用户领券是否达到上限，达到直接返回，如果是第一次领取，设置key的过期时间为优惠券的过期时间
        // 2.3 返回是否成功，已经用户的领券次数
        DefaultRedisScript<Long> luaScript = Singleton.get(STOCK_DECREMENT_AND_SAVE_USER_RECORD_LUA_PATH, () -> {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(STOCK_DECREMENT_AND_SAVE_USER_RECORD_LUA_PATH)));
            redisScript.setResultType(Long.class);
            return redisScript;
        });

        // 验证用户是否符合优惠券领取条件
        JSONObject claimRule = JSONUtil.parseObj(couponTemplate.getClaimRules());
        String limitPerPerson = claimRule.getStr("limitPerPerson");
        String couponTemplateCacheKey = String.format(COUPON_TEMPLATE_INFO_KEY, requestParam.getCouponTemplateId());
        // 用户缓存键
        String userCouponTemplateLimitCacheKey = String.format(USER_CLAIM_COUPON_COUNT, UserContext.getUserId(), requestParam.getCouponTemplateId());
        // 执行 LUA 脚本进行扣减库存以及增加 Redis 用户领券记录次数，获取结果（通过位运算获取两个结果）
        Long stockDecrementLuaResult = stringRedisTemplate.execute(
                luaScript,
                ListUtil.of(couponTemplateCacheKey, userCouponTemplateLimitCacheKey),
                String.valueOf(couponTemplate.getValidEndTime().getTime()), limitPerPerson
        );
        // 判断 LUA 脚本执行返回类，如果失败根据类型返回报错提示
        long firstField = StockDecrementReturnCombinedUtil.extractUserClaimCouponFirstField(stockDecrementLuaResult);
        if (RedisUserClaimCouponStatusEnum.isFail(firstField)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, RedisUserClaimCouponStatusEnum.fromType(firstField));
        }
        // 通过编程式事务执行优惠券库存自减以及增加用户优惠券领取记录
        long extractSecondField = StockDecrementReturnCombinedUtil.extractUserClaimCouponSecondField(stockDecrementLuaResult);
        transactionTemplate.executeWithoutResult(action -> {
            try {
                // 尝试扣减数据库库存
                int decremented = couponTemplateMapper.decrementCouponTemplateStock(couponTemplateId, 1);
                // 如果失败了说明库存不足
                if (!SqlHelper.retBool(decremented)) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存不足");
                }
                // 成功扣减，添加用户领券记录
                // 添加 Redis 用户领取的优惠券记录列表
                Date now = new Date();
                DateTime validEndTime = DateUtil.offsetHour(now, JSONUtil.parseObj(couponTemplate.getClaimRules()).getInt("validityPeriod"));
                UserCoupon userCoupon = UserCoupon.builder()
                        .receiveCount(Long.valueOf(extractSecondField).intValue())
                        .userId(userId)
                        .couponTemplateId(couponTemplateId)
                        .source(0)// 平台券
                        .status(CouponStatusEnum.ONLINE.getStatus())
                        .receiveTime(now)
                        .validEndTime(validEndTime)
                        .validStartTime(now)
                        .build();
                userCouponService.save(userCoupon);

                // TODO: 考虑将用户的优惠券详情放入到缓存中
            } catch (Exception e) {
                action.setRollbackOnly();
                // 识别异常
                if (e instanceof BusinessException) {
                    throw (BusinessException) e;
                }
                if (e instanceof DuplicateKeyException) {
                    log.error("用户重复领取优惠券，用户ID：{}，优惠券模板ID：{}", UserContext.getUserId(), requestParam.getCouponTemplateId());
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户重复领取优惠券");
                }
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "优惠券领取异常，请稍候再试");
            }
        });
    }
}




