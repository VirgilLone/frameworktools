package com.haoyunhu.tools.rest.annotation;

import java.lang.annotation.*;

/**
 * Created by weijun.hu on 2015/7/3.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestParam {
    //映射参数名
    String value() default "";
    //是否必须有值
    boolean required() default false;
}
