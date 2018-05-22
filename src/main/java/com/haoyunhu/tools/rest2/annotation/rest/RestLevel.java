package com.haoyunhu.tools.rest2.annotation.rest;

import java.lang.annotation.*;

/**
 * Created by weijun.hu on 2016/3/31.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestLevel {
    public static int LEVEL_ONE = 1;
    public static int LEVEL_TWO = 2;
    public static int LEVEL_THREE = 3;

    //映射参数名
    int level() default LEVEL_TWO;
}
