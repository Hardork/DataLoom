package com.hwq.bi.annotation;

import com.hwq.bi.model.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author:HWQ
 * @DateTime:2023/9/23 15:25
 * @Description:
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReduceRewardPoint {
    /**
     * 必须有某个角色
     *
     * @return
     */
    int reducePoint() default 1;

}
