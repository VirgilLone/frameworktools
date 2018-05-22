package com.haoyunhu.tools.logback.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.haoyunhu.tools.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by weijun.hu on 2016/1/13.
 */
public class LogisticThresholdFilter extends Filter<ILoggingEvent> {

    private String levels;
    private List<String> levelList = new ArrayList<>();

    public void setLevels(String levels) {
        this.levels = levels;
        if (StringUtils.isNotBlank(levels)) {
            levelList = Arrays.asList(StringUtils.split(levels, ";"));
        }
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if (levelList.contains(event.getLevel().levelStr)) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }

    public void start() {
        if (StringUtils.isNotBlank(levels)) {
            super.start();
        }
    }
}
