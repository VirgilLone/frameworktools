package com.haoyunhu.tools.rest2.mapper.convert;

import com.haoyunhu.tools.utils.DateUtils;
import org.modelmapper.AbstractConverter;

import java.util.Date;

/**
 * Created by weijun.hu on 2016/4/1.
 */
public class Date2StringConverter extends AbstractConverter<Date, String> {
    @Override
    protected String convert(Date source) {
        return source == null ? "" : DateUtils.dateToString(source);
    }
}
