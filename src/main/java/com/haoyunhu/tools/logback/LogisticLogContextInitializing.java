package com.haoyunhu.tools.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.haoyunhu.tools.cache.CacheDatabaseConfiguration;
import com.haoyunhu.tools.logback.bean.KibanaLogSettings;
import com.haoyunhu.tools.utils.CommonUtils;
import com.haoyunhu.tools.utils.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by weijun.hu on 2016/1/13.
 */
public class LogisticLogContextInitializing implements InitializingBean {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LogisticLogContextInitializing.class);

    private CacheDatabaseConfiguration cacheDatabaseConfiguration;
    private Map properties;
    private String filePath;
    @ApolloConfig
    private Config config;

    public void setCacheDatabaseConfiguration(CacheDatabaseConfiguration cacheDatabaseConfiguration) {
        this.cacheDatabaseConfiguration = cacheDatabaseConfiguration;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filePath);
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            loggerContext.reset();

            if (properties != null) {
                Set set = properties.keySet();
                for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();
                    String value = (String) properties.get(key);
                    if (cacheDatabaseConfiguration != null) {
                        loggerContext.putProperty(key, cacheDatabaseConfiguration.getString(value, ""));
                    } else {
                        loggerContext.putProperty(key, CommonUtils.getString(value));
                    }
                }
            }
            configurator.doConfigure(resourceAsStream);

            String somePublicNamespace = "test.logback";
            config = ConfigService.getConfig(somePublicNamespace);
            //String someKey = "someKeyFromPublicNamespace";
            //String someDefaultValue = "someDefaultValueForTheKey";
            //String value = config.getProperty(someKey, someDefaultValue);

            //InputStream inputStream = getClass().getClassLoader().getResourceAsStream("runtime.properties");
            //Properties p = new Properties();

            /*try {
                p.load(inputStream);
                inputStream.close();
            } catch (IOException var4) {
                var4.printStackTrace();
            }*/

            String initialUrl = config.getProperty("runtime.logServers","");
            int LevelLimit = Integer.parseInt(config.getProperty("runtime.level","1"));
            System.out.print("initialUrl-" + initialUrl + "\n");
            KibanaLogSettings.uriList = this.GenerateUri(initialUrl);
            KibanaLogSettings.Ip = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
            KibanaLogSettings.systemAlias = properties.get("runtime.systemAlias").toString();
            KibanaLogSettings.level = LevelLimit;
            System.out.print("systemAlias-" + KibanaLogSettings.systemAlias + "\n");

        } catch (Exception e) {
            logger.error("init logBack xml error , " + e.getMessage(), e);
        }
    }

    private ArrayList<String> GenerateUri(String initialUrl) {
        ArrayList list = new ArrayList();

        List<String> arrayList = StringUtils.toAddList(initialUrl);

        for (int i = 0; i < arrayList.size(); ++i) {
            list.add(arrayList.get(i));
        }
        return list;
    }

}
