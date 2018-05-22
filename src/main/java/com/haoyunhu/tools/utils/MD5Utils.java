package com.haoyunhu.tools.utils;

import java.security.MessageDigest;

/**
 * Created by junming.qi on 2015/7/22.
 */
public class MD5Utils {
    public static String encode(String str) {
        MessageDigest msgDigest = null;
        String md5Str = "";
        try {
            msgDigest = MessageDigest.getInstance("MD5");


            msgDigest.update(str.getBytes("utf-8"));
            byte[] bytes = msgDigest.digest();
            md5Str = new String();

            for (int i = 0; i < bytes.length; ++i) {
                byte tb = bytes[i];
                char tmpChar = (char) (tb >>> 4 & 15);
                char high;
                if (tmpChar >= 10) {
                    high = (char) (97 + tmpChar - 10);
                } else {
                    high = (char) (48 + tmpChar);
                }

                md5Str = md5Str + high;
                tmpChar = (char) (tb & 15);
                char low;
                if (tmpChar >= 10) {
                    low = (char) (97 + tmpChar - 10);
                } else {
                    low = (char) (48 + tmpChar);
                }

                md5Str = md5Str + low;
            }
        } catch (Exception e) {
            throw new IllegalStateException("System doesn\'t support MD5 algorithm.");
        }
        return md5Str;
    }
}
