package com.haoyunhu.tools.rest.cache;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * L3级别的token配置数据缓存
 */
public class RestTokenManager {

    private static Logger logger = Logger.getLogger(RestTokenManager.class);

    private static RestTokenManager configManager = null;

    private Map<String, String> cacheMap = new ConcurrentHashMap<>();

    // 缓存生命周期(默认)
    private Long life = 600000L;

    private RestTokenManager() {

    }

    public static synchronized RestTokenManager getInstance() {
        if (configManager == null) {
            configManager = new RestTokenManager();
        }
        return configManager;
    }

    public Map<String, String> getCacheMap() {
        return cacheMap;
    }

    public Long getLife() {
        return life;
    }

    public void setLife(Long life) {
        this.life = life;
    }

    public String get(String key) {
        return cacheMap.get(key);
    }

    public void put(String key, String value) {
        cacheMap.put(key, value);
    }

    public void clearCacheTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (cacheMap) {
                    cacheMap.clear();
                }
            }
        }, 5000, life);
    }
}
