package com.haoyunhu.tools.rest2.annotation.validtor;

import java.lang.annotation.*;

/**
 * Created by weijun.hu on 2016/4/12.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogisticsRestNotNull {
    String message() default "";
}
