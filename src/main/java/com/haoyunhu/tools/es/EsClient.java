package com.haoyunhu.tools.es;

import com.haoyunhu.tools.utils.DateUtils;
import org.elasticsearch.client.transport.TransportClient;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Created by zg on 2017/1/4.
 */
public class EsClient extends TransportClient {

    public final static String appLogType = "applog";
    public final static String appLogPrefix = "application-log-java";

    private EsClient() {

    }

    public static void writeLog(TransportClient client, String appLog) throws IOException {
        String indexName = MessageFormat.format("{0}-{1}", appLogPrefix, DateUtils.getDateFormate(new Date(), "yyyy.MM.dd"));
        client.prepareIndex(indexName, appLogType).setSource(appLog).get();
    }
}
