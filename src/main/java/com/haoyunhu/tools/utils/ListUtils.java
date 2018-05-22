package com.haoyunhu.tools.utils;

import java.util.List;

/**
 * Created by weijun.hu on 2015/6/19.
 */
public class ListUtils {

    /**
     * 判断是否不为空
     */
    public static Boolean isNotEmpty(List o) {
        return o != null && o.size() > 0;
    }

    /**
     * 判断是否为空
     * @return
     */
    public static Boolean isEmpty(List o) {
        return !isNotEmpty(o);
    }
}
