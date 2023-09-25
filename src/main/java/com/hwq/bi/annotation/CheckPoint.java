package com.hwq.bi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author:HWQ
 * @DateTime:2023/9/23 15:46
 * @Description:
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPoint {
    /**
     * 服务要的积分
     * @return
     */
    int needPoint() default 1;
}
