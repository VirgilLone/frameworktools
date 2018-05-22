package com.haoyunhu.tools.utils;


import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SecurityUtils {

    private static Logger logger = Logger.getLogger(SecurityUtils.class);

    // 加密得到sign
    public static String getMd5Sign(Map<String, Object> map, String secretKey) {
        // 第一步：把字典按Key的字母顺序排序
        Set<String> sets = new TreeSet<String>();
        Set<String> mapSets = map.keySet();
        for (String item : mapSets) {
            sets.add(item);
        }
        sets.remove("sign");// 移除加密字符串

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder parames = new StringBuilder();
        for (String item : sets) {
            Object value = map.get(item);
            if(value != null){
                parames.append(item).append(value);
            }
        }
        parames.append(secretKey);

        // 第三步：使用MD5加密
        logger.debug("MD5 params : " + parames.toString());
        String md5 = MD5Utils.encode(parames.toString());

        // 第四步：把二进制转化为大写
        return md5.toUpperCase();
    }

}
