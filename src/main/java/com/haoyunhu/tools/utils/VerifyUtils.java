package com.haoyunhu.tools.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by te.huang on 2015/12/7.
 */
public class VerifyUtils {
    private VerifyUtils(){
    }
    // 判断手机号格式是否正确
    public static boolean validateTelFormat(String telephone){
        if(StringUtils.isNotEmpty(telephone)){
            Pattern pattern = Pattern.compile("^1[34587][0-9]{9}$");
            Matcher matcher = pattern.matcher(telephone);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    //判断身份证号码是否有效
    public static boolean validateIdCardFormat(String idCard){
        if ((StringUtils.isNotEmpty(idCard))){
            Pattern pattern=Pattern.compile("/(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)/");
            Matcher matcher=pattern.matcher(idCard);
            if (matcher.find()){
                return true;
            }
        }
        return false;
    }
}
