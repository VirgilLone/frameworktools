package com.haoyunhu.tools.utils;

import java.util.Random;

public class RandomUtils {

    public static final Random random = new Random();

    /**
     * 获取区间的随机数
     */
    public static final int getRandom(int min, int max) {
        int result = 0;
        // 若传值错误，直接返回最小值
        if (min >= max) {
            return min;
        }
        // 防止死循环
        for (int i = 0; i < 50; i++) {
            result = random.nextInt(max);
            if (result >= min) {
                break;
            }
        }
        // 若循环50次，依然未得到合适的数值，返回最小值
        if (result < min) {
            result = min;
        }
        return result;
    }
}
