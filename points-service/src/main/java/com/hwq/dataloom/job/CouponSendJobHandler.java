package com.hwq.dataloom.job;

import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.service.InnerUserServiceInterface;
import com.hwq.dataloom.service.CouponTemplateService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 每周定时发放优惠券
 */
@Component
public class CouponSendJobHandler {

    @Resource
    private CouponTemplateService couponTemplateService;  // 假设有一个服务处理发放优惠券的逻辑

    @Resource
    private InnerUserServiceInterface innerUserServiceInterface;


    @XxlJob("weeklyCouponSendJobHandler")
    public void execute() throws Exception {
        // 分批发放用户每周优惠券，避免一次性取出太多用户导致OOM
        distributeCoupons(1000);
    }

    /**
     * 分批发放优惠券
     * @param batchSize 每批处理的用户数量
     */
    public void distributeCoupons(int batchSize) {
//        int offset = 0;
//        List<User> userBatch;
//        do {
//            // 分批获取用户信息
//            userBatch = innerUserServiceInterface.findUsersByBatch(offset, batchSize);
//            offset += batchSize;
//
//            // 发放优惠券
//            for (User user : userBatch) {
//                sendCouponToUser(user, couponTemplateId);
//            }
//
//        } while (!userBatch.isEmpty()); // 当批次用户列表为空时，停止循环
    }
}