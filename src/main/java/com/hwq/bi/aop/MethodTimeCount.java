package com.hwq.bi.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @author HWQ
 * @date 2024/3/28 23:06
 * @description
 */
@Aspect
@Component
public class MethodTimeCount {
    @Around("execution(* com.hwq.bi.controller.ChartController.getChartById())")
    public Object doCount(ProceedingJoinPoint point) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object proceed = point.proceed();
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        System.out.println("耗时ms数:" + totalTimeMillis);
        return proceed;
    }
}
