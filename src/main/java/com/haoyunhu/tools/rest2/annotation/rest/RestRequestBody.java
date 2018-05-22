package com.haoyunhu.tools.rest2.annotation.rest;

import java.lang.annotation.*;

/**
 * Created by weijun.hu on 2016/3/31.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestRequestBody {
}
