package com.haoyunhu.tools.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class BigDecimalUtils {

    private BigDecimalUtils(){

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

    public static BigDecimal getBigDecimal(String value) {
        if (StringUtils.isBlank(value)) {
            return BigDecimal.valueOf(0);
        }
        return new BigDecimal(value);
    }

    /**
     * 是否小于
     *
     * @param firstValue
     * @param secondValue
     * @return
     */
    public static Boolean isLessThan(BigDecimal firstValue, BigDecimal secondValue) {
        if (firstValue == null || secondValue == null) {
            return false;
        } else {
            return firstValue.compareTo(secondValue) < 0;
        }

    }

    /**
     * 是否等于
     *
     * @param firstValue
     * @param secondValue
     * @return
     */
    public static Boolean isEquals(BigDecimal firstValue, BigDecimal secondValue) {
        if (firstValue == null || secondValue == null) {
            return false;
        } else {
            return firstValue.compareTo(secondValue) == 0;
        }

    }

    /**
     * 是否大于
     *
     * @param firstValue
     * @param secondValue
     * @return
     */
    public static Boolean isMoreThan(BigDecimal firstValue, BigDecimal secondValue) {
        if (firstValue == null || secondValue == null) {
            return false;
        } else {
            return firstValue.compareTo(secondValue) > 0;
        }

    }
}
