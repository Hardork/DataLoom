package com.hwq.dataloom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.config.UserContext;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.dto.coupon_remind.CouponTemplateRemindCreateReqDTO;
import com.hwq.dataloom.model.entity.CouponTemplate;
import com.hwq.dataloom.model.entity.CouponTemplateRemind;
import com.hwq.dataloom.mq.event.CouponRemindEvent;
import com.hwq.dataloom.mq.producer.CouponRemindProducer;
import com.hwq.dataloom.service.CouponTemplateRemindService;
import com.hwq.dataloom.mapper.CouponTemplateRemindMapper;
import com.hwq.dataloom.service.CouponTemplateService;
import com.hwq.dataloom.utils.CouponTemplateRemindUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author wqh
* @description 针对表【coupon_template_remind(用户预约提醒信息存储表)】的数据库操作Service实现
* @createDate 2024-09-04 02:39:34
*/
@Service
public class CouponTemplateRemindServiceImpl extends ServiceImpl<CouponTemplateRemindMapper, CouponTemplateRemind>
    implements CouponTemplateRemindService{

    @Resource
    private CouponTemplateService couponTemplateService;

    @Resource
    private CouponRemindProducer couponRemindProducer;


    @Override
    public Boolean createCouponRemind(CouponTemplateRemindCreateReqDTO requestParam) {
        // 获取用户信息
        Long userId = UserContext.getUserId();
        // 判断对应优惠券是否存在
        Long couponTemplateId = requestParam.getCouponTemplateId();
        CouponTemplate couponTemplate = couponTemplateService.getById(couponTemplateId);
        ThrowUtils.throwIf(couponTemplate == null, ErrorCode.NOT_FOUND_ERROR, "不存在对应优惠券模版");
        // 查询是否已经创建过优惠券
        LambdaQueryWrapper<CouponTemplateRemind> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(CouponTemplateRemind::getCouponTemplateId, couponTemplateId)
                .eq(CouponTemplateRemind::getUserId, userId);
        CouponTemplateRemind remind = this.getOne(wrapper);
        if (remind == null) { // 不存在提醒
            CouponTemplateRemind couponTemplateRemind = CouponTemplateRemind.builder()
                    .couponTemplateId(couponTemplateId)
                    .information(CouponTemplateRemindUtil.calculateBitMap(requestParam.getRemindTime(), requestParam.getType()))
                    .userId(userId)
                    // 设置开始提醒的时间
                    .startTime(DateUtil.offsetMinute(couponTemplate.getValidStartTime(), requestParam.getRemindTime()))
                    .build();
            this.save(couponTemplateRemind);
        } else { // 如果不存在就更新
            Long information = remind.getInformation();
            Long bitMap = CouponTemplateRemindUtil.calculateBitMap(requestParam.getRemindTime(), requestParam.getType());
            ThrowUtils.throwIf((information & bitMap) != 0L, ErrorCode.PARAMS_ERROR, "已经创建过该提醒了");
            remind.setInformation(information ^ bitMap);
            this.updateById(remind);
        }
        // 将消息推送到延迟队列中
        CouponRemindEvent remindEvent = BeanUtil.toBean(requestParam, CouponRemindEvent.class);
        remindEvent.setStartTime(couponTemplate.getValidStartTime());
        remindEvent.setUserId(userId);
        couponRemindProducer.sendMessage(remindEvent);
        return true;
    }
}




