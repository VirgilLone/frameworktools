package com.haoyunhu.tools.rest2.mapper.convert;

import org.modelmapper.AbstractConverter;

/**
 * Created by weijun.hu on 2016/4/1.
 */
public class Boolean2StringConverter extends AbstractConverter<Boolean, String> {
    @Override
    protected String convert(Boolean source) {
        return source == null ? "" : source.toString();
    }
}
