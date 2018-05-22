package com.haoyunhu.tools.cache;

import com.haoyunhu.tools.utils.HttpUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置数据缓存
 */
public class ConfigManager {

    private static Logger logger = Logger.getLogger(ConfigManager.class);

    private static ConfigManager configManager = null;

    private Map<String, String> cacheMap = new ConcurrentHashMap<>();

    private ConfigManager() {

    }

    public static synchronized ConfigManager getInstance() {
        if (configManager == null) {
            configManager = new ConfigManager();
        }
        return configManager;
    }

    public Map<String, String> getCacheMap() {
        return cacheMap;
    }

    public static String get(String key) {
        ConfigManager instance = getInstance();
        if (instance == null) {
            return null;
        }
        return instance.getCacheMap().get(key);
    }

    //读取CONFIG相关配置
    public void reloadProperties(String filePath) {
        InputStreamReader configStream = null;
        try {
            configStream = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filePath), HttpUtils.UTF8);
            Properties configProperties = new Properties();
            configProperties.load(configStream);
            Set<Object> configObjSet = configProperties.keySet();
            Map<String, String> cacheMap = getCacheMap();
            for (Iterator<Object> iterator = configObjSet.iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                cacheMap.put(key, configProperties.getProperty(key));
            }
        } catch (IOException e) {
            logger.info("reload properties error.", e);
        } finally {
            if(configStream != null){
                try {
                    configStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("close configStream error.", e);
                }
            }
        }
    }

}
