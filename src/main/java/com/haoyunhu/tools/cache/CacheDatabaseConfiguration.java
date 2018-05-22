package com.haoyunhu.tools.cache;

import org.apache.commons.configuration.DatabaseConfiguration;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by weijun.hu on 2015/12/8.
 */
public class CacheDatabaseConfiguration extends DatabaseConfiguration implements Serializable {

    private static final long serialVersionUID = -7354047734018031928L;

    private static final Logger logger = Logger.getLogger(CacheDatabaseConfiguration.class);

    // 缓存池
    private Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

    // 缓存生命周期(默认)
    private Long life = 600000L;

    public CacheDatabaseConfiguration(DataSource datasource, String table, String nameColumn, String keyColumn, String valueColumn, String name) {
        super(datasource, table, nameColumn, keyColumn, valueColumn, name);
    }

    public CacheDatabaseConfiguration(DataSource datasource, String table, String keyColumn, String valueColumn) {
        super(datasource, table, keyColumn, valueColumn);
    }

    public long getLife() {
        return life;
    }

    public void setLife(Long life) {
        this.life = life;
    }

    @Override
    public void addProperty(String key, Object value) {
        super.addProperty(key, value);
        cache.put(key, value);
    }

    @Override
    public boolean containsKey(String arg0) {
        return cache.containsKey(arg0) ? true : super.containsKey(arg0);
    }

    @Override
    public Object getProperty(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            Object result = super.getProperty(key);
            if (result != null) {
                cache.put(key, result);
                return result;
            } else {
                logger.debug("application config can not found the key \"" + key + "\"in database");
            }
        }
        return null;
    }

    /**
     * 启动心跳，定时清理缓存
     */
    public void clearCacheTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (cache) {
                    cache.clear();
                }
            }
        }, 5000, life);
    }

}
