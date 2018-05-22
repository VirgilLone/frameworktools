package com.haoyunhu.tools.es;

import com.haoyunhu.tools.logback.bean.KibanaLogSettings;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zg on 2017/1/4.
 */
public class EsClientUtils {

    private static TransportClient client;

    public static synchronized TransportClient getInstance() throws UnknownHostException {
        if (client == null) {

            Settings settings = ImmutableSettings.settingsBuilder()
                    .put("client.transport.sniff", true)
                    .put("client", true)
                    .put("data", false)
                    .put("cluster.name", "eslog")
                    .build();
            client = new TransportClient(settings);
            for (String host : KibanaLogSettings.uriList) {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
            }
            return client;
        } else {
            return client;
        }
    }
}
