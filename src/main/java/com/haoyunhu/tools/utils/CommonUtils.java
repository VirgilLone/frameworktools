package com.haoyunhu.tools.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * Created by cheng.hu on 2015/8/31.
 */
public class CommonUtils {
    private CommonUtils() {
    }

    public static String add(String one, String two) {
        if (StringUtils.isBlank(one)) {
            one = "0";
        }
        if (StringUtils.isBlank(two)) {
            two = "0";
        }
        BigDecimal result = new BigDecimal(one).add(new BigDecimal(two));
        return String.valueOf(result);
    }

    public static String getString(BigDecimal value) {
        if (value == null) {
            return "0";
        } else {
            return String.valueOf(value);
        }
    }

    public static String getString(String value) {
        if (value == null) {
            return "";
        } else {
            return String.valueOf(value);
        }
    }

    public static BigDecimal getBigDecimal(String value) {
        if (StringUtils.isBlank(value)) {
            return BigDecimal.valueOf(0);
        }
        return new BigDecimal(value);
    }

    public static BigDecimal getBigDecimal(BigDecimal value) {
        if (null == value) {
            return BigDecimal.valueOf(0);
        }
        return value;
    }

    public static BigDecimal getBigDecimal(Integer value) {
        if (null == value) {
            return BigDecimal.valueOf(0);
        }
        return new BigDecimal(value);
    }

    public static BigDecimal getBigDecimal(Object value) {
        if (null == value) {
            return BigDecimal.valueOf(0);
        }
        return new BigDecimal(value.toString());
    }

    public static String getString(Object value) {
        if (null == value) {
            return "";
        }
        return value.toString();
    }

    public static Integer getInteger(String value) {
        if (StringUtils.isBlank(value)) {
            return Integer.valueOf(0);
        }
        return Integer.valueOf(value);
    }
}
