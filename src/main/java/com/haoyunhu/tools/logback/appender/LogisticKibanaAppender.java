package com.haoyunhu.tools.logback.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import com.haoyunhu.tools.es.EsClient;
import com.haoyunhu.tools.es.EsClientUtils;
import com.haoyunhu.tools.logback.bean.AppLog;
import com.haoyunhu.tools.logback.bean.KibanaLogSettings;
import com.haoyunhu.tools.utils.JacksonUtils;
import com.haoyunhu.tools.utils.DateUtils;

import java.util.Date;

/**
 * Created by zg on 2016/12/15.
 */
public class LogisticKibanaAppender extends AppenderBase<LoggingEvent> {
    @Override
    protected void append(LoggingEvent eventObject) {
        int levelLimit = this.convertTo(eventObject.getLevel());
        if (levelLimit >= KibanaLogSettings.level) {
            AppLog appLog = new AppLog();
            appLog.setTopic(eventObject.getLoggerName());
            appLog.setCreatedDate(new Date(eventObject.getTimeStamp()));
            appLog.setLevel(levelLimit);
            appLog.setTitle(eventObject.getThreadName());
            appLog.setCreatedDate(new Date());
            appLog.setCreatedDateString(DateUtils.getNow(new Date()));
            StringBuilder buf = new StringBuilder(256);
            buf.append(eventObject.getMessage());
            IThrowableProxy tp = eventObject.getThrowableProxy();
            if (tp != null) {
                String callerDataArray = ThrowableProxyUtil.asString(tp);
                buf.append(',');
                buf.append(callerDataArray);
            }

            StackTraceElement[] callerDataArray1 = eventObject.getCallerData();
            if (callerDataArray1 != null && callerDataArray1.length > 0) {
                buf.append(',');
                buf.append("\"location\":{");
                StackTraceElement immediateCallerData = callerDataArray1[0];
                buf.append("class:" + immediateCallerData.getClassName());
                buf.append(',');
                buf.append("method:" + immediateCallerData.getMethodName());
                buf.append(',');
                buf.append("file:" + immediateCallerData.getFileName());
                buf.append(',');
                buf.append("line:" + Integer.toString(immediateCallerData.getLineNumber()));
                buf.append("}");
            }

            appLog.setContent(buf.toString());
            sendLogs(appLog);
        }
    }

    private int convertTo(Level level) {
        return !level.equals(Level.DEBUG) && !level.equals(Level.TRACE) ? (level.equals(Level.INFO) ? 1 : (level.equals(Level.WARN) ? 2 : (level.equals(Level.ERROR) ? 3 : 0))) : 0;
    }

    private void sendLogs(AppLog logs) {
        try {
            EsClient.writeLog(EsClientUtils.getInstance(), JacksonUtils.getInstance().writeValueAsString(logs));
        } catch (Exception var10) {
            var10.printStackTrace();
        }
    }
}
