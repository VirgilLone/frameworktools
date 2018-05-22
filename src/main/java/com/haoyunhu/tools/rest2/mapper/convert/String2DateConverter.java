package com.haoyunhu.tools.rest2.mapper.convert;

import com.haoyunhu.tools.utils.DateUtils;
import com.haoyunhu.tools.utils.StringUtils;
import org.modelmapper.AbstractConverter;

import java.util.Date;

/**
 * Created by weijun.hu on 2016/4/1.
 */
public class String2DateConverter extends AbstractConverter<String, Date> {
    @Override
    protected Date convert(String source) {
        return StringUtils.isBlank(source) ? null : DateUtils.stringToDate(source, DateUtils.FORMAT);
    }
}
