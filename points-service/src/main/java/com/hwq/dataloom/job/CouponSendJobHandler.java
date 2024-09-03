package com.hwq.dataloom.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.framework.service.InnerUserServiceInterface;
import com.hwq.dataloom.mapper.CouponTaskMapper;
import com.hwq.dataloom.model.entity.CouponTask;
import com.hwq.dataloom.model.enums.CouponTaskStatusEnum;
import com.hwq.dataloom.mq.event.ScheduledCouponEvent;
import com.hwq.dataloom.mq.producer.ScheduledCouponProducer;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.swagger.v3.oas.annotations.Operation;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 每周定时发放优惠券
 */
@Component
@RestController
@RequestMapping("/xxl-job")
public class CouponSendJobHandler extends IJobHandler {


    @Resource
    private InnerUserServiceInterface innerUserServiceInterface;

    @Resource
    private ScheduledCouponProducer scheduledCouponProducer;


    @Resource
    private CouponTaskMapper couponTaskMapper;

    /**
     * 一次最大进行处理任务数
     */
    private static final int MAX_LIMIT = 100;


    @SneakyThrows
    @Operation(summary = "执行优惠券定时推送") // 为了保障快速启动，可通过 Swagger 方式访问接口，可以减少一个中间件 XXL-Job
    @GetMapping("/coupon-task/job") // 为了保障快速启动，可通过 Swagger 方式访问接口，可以减少一个中间件 XXL-Job
    public BaseResponse<Void> webExecute() {
        execute();
        return ResultUtils.success();
    }

    @XxlJob("couponTemplateTask")
    public void execute() throws Exception {
        // 扫描任务表，发现定时任务类型的任务
        // 将扫描到的任务推送到消息队列中进行消费
        Long initId = 0L;
        Date now = new Date();
        while (true) {
            List<CouponTask> couponTasks = fetchPendingTasks(initId, now);
            if (couponTasks.isEmpty()) {
                break;
            }
            // 将任务推送到队列中进行处理
            for (CouponTask couponTask : couponTasks) {
                ScheduledCouponEvent event = ScheduledCouponEvent.builder()
                        .couponTaskId(couponTask.getId())
                        .distributeType(1)
                        .build();
                scheduledCouponProducer.sendMessage(event);
            }
            if (couponTasks.size() < MAX_LIMIT) {
                break;
            }
            // 更新init
            initId = couponTasks.stream()
                    .map(CouponTask::getId)
                    .max(Long::compare)
                    .orElse(initId);
        }
    }

    /**
     * 从大于initId的记录开始查询
     * @param initId 起始查询id
     * @param now 现在时间
     * @return 到点的定时任务
     */
    private List<CouponTask> fetchPendingTasks(Long initId, Date now) {
        LambdaQueryWrapper<CouponTask> queryWrapper = Wrappers.lambdaQuery(CouponTask.class)
                .eq(CouponTask::getStatus, CouponTaskStatusEnum.WAIT_EXE.getStatus())
                .le(CouponTask::getSendTime, now)
                .gt(CouponTask::getId, initId)
                .last("LIMIT " + MAX_LIMIT);
        return couponTaskMapper.selectList(queryWrapper);
    }
}