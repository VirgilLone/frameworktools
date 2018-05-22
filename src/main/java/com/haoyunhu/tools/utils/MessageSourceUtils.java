package com.haoyunhu.tools.utils;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * Created by weijun.hu on 2015/7/6.
 */
public class MessageSourceUtils {

    private ResourceBundleMessageSource messageSource;

    public void setMessageSource(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code, Object[] args, String defaultMessage) {
        String msg = messageSource.getMessage(code, args, defaultMessage, Locale.SIMPLIFIED_CHINESE);
        return msg != null ? msg.trim() : msg;
    }

    public String getMessage(String code, Object[] args) {
        String msg = messageSource.getMessage(code, args, null);
        return msg != null ? msg.trim() : msg;
    }

    public String getMessage(String code) {
        String msg = messageSource.getMessage(code, null, null);
        return msg != null ? msg.trim() : msg;
    }
}
