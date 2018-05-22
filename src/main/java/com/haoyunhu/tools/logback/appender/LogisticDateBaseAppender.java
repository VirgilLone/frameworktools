package com.haoyunhu.tools.logback.appender;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.db.DBAppenderBase;
import ch.qos.logback.core.db.DBHelper;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.Date;

/**
 * Created by weijun.hu on 2015/8/21.
 */

public class LogisticDateBaseAppender extends DBAppenderBase<LoggingEvent> {

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
        return "";
    }

    protected String getInsertSQL(String level) {
        StringBuilder sqlSB = new StringBuilder("insert into ").append(" T_LOG_").append(level)
                .append(" (LOG_PROJECT, LOG_CODE,LOG_CLASS, LOG_TITLE, LOG_INFO,LOG_DATE, LOG_CREATE_DATE) values(?,?,?,?,?,?,?)");
        return sqlSB.toString();
    }

    @Override
    public void append(LoggingEvent eventObject) {
        Connection connection = null;
        try {
            connection = connectionSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement insertStatement;

            if (cnxSupportsGetGeneratedKeys) {
                String EVENT_ID_COL_NAME = "EVENT_ID";
                // see
                if (connectionSource.getSQLDialectCode() == SQLDialectCode.POSTGRES_DIALECT) {
                    EVENT_ID_COL_NAME = EVENT_ID_COL_NAME.toLowerCase();
                }
                insertStatement = connection.prepareStatement(getInsertSQL(eventObject.getLevel().levelStr),
                        new String[] { EVENT_ID_COL_NAME });
            } else {
                insertStatement = connection.prepareStatement(getInsertSQL(eventObject.getLevel().levelStr));
            }

            long eventId;
            // inserting an event and getting the result must be exclusive
            synchronized (this) {
                subAppend(eventObject, connection, insertStatement);
                eventId = selectEventId(insertStatement, connection);
            }
            secondarySubAppend(eventObject, connection, eventId);

            // we no longer need the insertStatement
            close(insertStatement);

            connection.commit();
        } catch (Throwable sqle) {
            addError("problem appending event", sqle);
        } finally {
            DBHelper.closeConnection(connection);
        }
    }

    void close(Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
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

        preparedStatement.setString(1, getProjectType());
        preparedStatement.setString(2, loggingEvent.getMarker() != null ? loggingEvent.getMarker().getName() : null);
        preparedStatement.setString(3, loggingEvent.getLoggerName());
        preparedStatement.setString(4, message);
        preparedStatement.setString(5, exDetailSB.toString());
        preparedStatement.setTimestamp(6, new Timestamp(loggingEvent.getTimeStamp()));
        preparedStatement.setTimestamp(7, new Timestamp(new Date().getTime()));
        preparedStatement.executeUpdate();
    }

    @Override
    protected void secondarySubAppend(LoggingEvent loggingEvent, Connection connection, long l) throws Throwable {

    }
}
