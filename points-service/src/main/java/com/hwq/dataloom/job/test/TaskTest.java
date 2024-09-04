package com.hwq.dataloom.job.test;

import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author HWQ
 * @date 2024/9/4 13:56
 * @description
 */
public class TaskTest {
    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello, 被触发");
            }
        }, DateUtil.offsetSecond(new Date(), 5));
    }
}
