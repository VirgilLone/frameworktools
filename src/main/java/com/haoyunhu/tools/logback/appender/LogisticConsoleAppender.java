package com.haoyunhu.tools.logback.appender;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import ch.qos.logback.core.status.ErrorStatus;
import com.haoyunhu.tools.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by weijun.hu on 2015/8/21.
 */

public class LogisticConsoleAppender extends ConsoleAppender<LoggingEvent> {

    private String filterClassNames;
    private List<String> filterClassNameList = new ArrayList<>();

    public void setFilterClassNames(String filterClassNames) {
        this.filterClassNames = filterClassNames;
        if(StringUtils.isNotBlank(filterClassNames)){
            filterClassNameList = Arrays.asList(StringUtils.split(filterClassNames, ","));
        }
    }

    protected void subAppend(LoggingEvent event) {
        //过滤
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if(throwableProxy != null){
            String className = throwableProxy.getClassName();
            if(filterClassNameList.contains(className)){
                return;
            }
        }

        if (!isStarted()) {
            return;
        }
        try {
            // this step avoids LBCLASSIC-139
            if (event instanceof DeferredProcessingAware) {
                ((DeferredProcessingAware) event).prepareForDeferredProcessing();
            }
            // the synchronization prevents the OutputStream from being closed while we
            // are writing. It also prevents multiple threads from entering the same
            // converter. Converters assume that they are in a synchronized block.
            lock.lock();
            try {
                writeOut(event);
            } finally {
                lock.unlock();
            }
        } catch (IOException ioe) {
            // as soon as an exception occurs, move to non-started state
            // and add a single ErrorStatus to the SM.
            this.started = false;
            addStatus(new ErrorStatus("IO failure in appender", this, ioe));
        }
    }

}
