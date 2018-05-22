package com.haoyunhu.tools.redis;

import com.haoyunhu.tools.cache.CacheDatabaseConfiguration;
import com.haoyunhu.tools.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by junming.qi on 2016/2/25.
 */
public class JedisPoolConfigInitializing extends JedisPoolConfig implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisPoolConfigInitializing.class);

    private CacheDatabaseConfiguration cacheDatabaseConfiguration;
    private String maxIdleConfigDBKey;
    private String maxTotalConfigDBKey;
    private String maxWaitMillisConfigDBKey;
    private String testOnBorrowConfigDBKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        setMaxIdle(Integer.valueOf(getDBConfigByKey(maxIdleConfigDBKey, String.valueOf(100))));
        setMaxTotal(Integer.valueOf(getDBConfigByKey(maxTotalConfigDBKey, String.valueOf(300))));
        setMaxWaitMillis(Integer.valueOf(getDBConfigByKey(maxTotalConfigDBKey, String.valueOf(1000))));
        setTestOnBorrow(getDBConfigByKey(testOnBorrowConfigDBKey, "1").equals("1") ? true : false);
    }

    public String getDBConfigByKey(String key, String defaultValue) {
        if (StringUtils.isBlank(key) || cacheDatabaseConfiguration == null) {
            return defaultValue;
        }
        return cacheDatabaseConfiguration.getString(key, defaultValue);
    }

    public String getMaxIdleConfigDBKey() {
        return maxIdleConfigDBKey;
    }

    public void setMaxIdleConfigDBKey(String maxIdleConfigDBKey) {
        this.maxIdleConfigDBKey = maxIdleConfigDBKey;
    }

    public String getMaxTotalConfigDBKey() {
        return maxTotalConfigDBKey;
    }

    public void setMaxTotalConfigDBKey(String maxTotalConfigDBKey) {
        this.maxTotalConfigDBKey = maxTotalConfigDBKey;
    }

    public String getMaxWaitMillisConfigDBKey() {
        return maxWaitMillisConfigDBKey;
    }

    public void setMaxWaitMillisConfigDBKey(String maxWaitMillisConfigDBKey) {
        this.maxWaitMillisConfigDBKey = maxWaitMillisConfigDBKey;
    }

    public String getTestOnBorrowConfigDBKey() {
        return testOnBorrowConfigDBKey;
    }

    public void setTestOnBorrowConfigDBKey(String testOnBorrowConfigDBKey) {
        this.testOnBorrowConfigDBKey = testOnBorrowConfigDBKey;
    }

    public CacheDatabaseConfiguration getCacheDatabaseConfiguration() {
        return cacheDatabaseConfiguration;
    }

    public void setCacheDatabaseConfiguration(CacheDatabaseConfiguration cacheDatabaseConfiguration) {
        this.cacheDatabaseConfiguration = cacheDatabaseConfiguration;
    }


}
