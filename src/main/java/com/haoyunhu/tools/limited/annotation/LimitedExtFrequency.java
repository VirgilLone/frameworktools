package com.haoyunhu.tools.limited.annotation;

import java.lang.annotation.*;

/**
 * 加强对方法限制
 * 根据被制定方法中的维度keyName限制多少时间内允许的请求次数
 * Created by weijun.hu on 2016/3/31.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitedExtFrequency {
    //需要幂等的keyName 有多个,当空的情况下就是全部
    String params() default "";
    //需要排除掉的keyName
    String excludeParams() default "";
    //限制次数
    int count() default 1;
    //多少时间内限制
    long timeout() default 60;
    //限制提示信息
    String message() default "";
}
