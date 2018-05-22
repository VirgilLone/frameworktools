package com.haoyunhu.tools.rest2.annotation.limited;

import java.lang.annotation.*;

/**
 * 业务幂等，多少时间内不能重复提交
 * Created by weijun.hu on 2016/3/31.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitedIdempotent {
    //需要幂等的keyName 有多个,当空的情况下就是全部
    String params() default "";
    //需要排除掉的keyName
    String excludeParams() default "";
    //幂等时间
    long timeout() default 3;
    //幂等提示信息
    String message() default "";
}
