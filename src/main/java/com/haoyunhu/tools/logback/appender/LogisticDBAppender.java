package com.haoyunhu.tools.logback.appender;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.db.DBAppenderBase;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/**
 * Created by weijun.hu on 2015/8/21.
 */

public class LogisticDBAppender extends DBAppenderBase<LoggingEvent> {

    private String projectType;

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    @Override
    protected Method getGeneratedKeysMethod() {
        return null;
    }

    @Override
    protected String getInsertSQL() {
        return "insert into T_LOG_ERROR (ERROR_ID, ERROR_LEVEL, ERROR_EXCEPTIONCODE, \n" +
                "      ERROR_CLASS, ERROR_SOURCE, CREATEDATE, \n" +
                "      CREATEUSERID, CREATEUSERNAME, ERROR_PROJECT, \n" +
                "      ERROR_MESSAGE, ERROR_EXCEPTION)\n" +
                "    values (SQ_T_LOG_ERROR.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
    }

    @Override
    protected void subAppend(LoggingEvent loggingEvent, Connection connection, PreparedStatement preparedStatement) throws Throwable {
        StringBuffer exDetailSB = new StringBuffer();
        IThrowableProxy throwableProxy = loggingEvent.getThrowableProxy();
        if (throwableProxy != null && throwableProxy.getStackTraceElementProxyArray() != null) {
            for (StackTraceElementProxy stackTraceElementProxy : throwableProxy.getStackTraceElementProxyArray()) {
                exDetailSB.append(stackTraceElementProxy.getSTEAsString()).append("\r\n");
            }
        }else{
            exDetailSB.append(loggingEvent.getMessage());
        }

        String message = loggingEvent.getMessage();
        if(StringUtils.isBlank(message)){
            message = "NullPointerException";
        }

        preparedStatement.setString(1, loggingEvent.getLevel().levelStr);
        preparedStatement.setString(2, loggingEvent.getMarker() != null ? loggingEvent.getMarker().getName() : null);
        preparedStatement.setString(3, null);
        preparedStatement.setString(4, null);
        preparedStatement.setTimestamp(5, new Timestamp(loggingEvent.getTimeStamp()));
        preparedStatement.setString(6, null);
        preparedStatement.setString(7, null);
        preparedStatement.setString(8, getProjectType());
        preparedStatement.setString(9, message);
        preparedStatement.setString(10, exDetailSB.toString());
        preparedStatement.executeUpdate();
    }

    @Override
    protected void secondarySubAppend(LoggingEvent loggingEvent, Connection connection, long l) throws Throwable {

    }
}
