package com.haoyunhu.tools.rest.bean;

import com.haoyunhu.tools.cache.CacheDatabaseConfiguration;
import com.haoyunhu.tools.redis.RedisService;
import com.haoyunhu.tools.utils.StringUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by weijun.hu on 2015/11/24.
 */
public class RestControllerBeanProperties implements InitializingBean {

    private CacheDatabaseConfiguration cacheDatabaseConfiguration;

    //是否开启性能监控
    private String isUsePerformance;
    private String isUsePerformanceDBKey;
    //是否开启接口验证
    private String isUseHttpValid;
    private String isUseHttpValidDBKey;
    //是否开启打印DEBUG(默认开启)
    private String isShowDebugLog = "1";

    //L3 token缓存时间
    private String tokenCacheLife;
    //是否启用L3协议
    private String isUseL3Valid;
    //SSO请求地址
    private String ssoCenterUrl;
    private String ssoCenterUrlDBKey;
    //SSO请求项目
    private String ssoCenterChannel;
    private String ssoCenterChannelDBKey;
    //SSO请求类型
    private String ssoCenterType;
    private String ssoCenterTypeDBKey;

    //接口标示
    private String appId;
    private String appIdDBKey;
    //接口密钥
    private String secretKey;
    private String secretKeyDBKey;

    //接口配置文件路径
    private String restServicePath;

    //是否返回时间
    private String isShowResponseTime;
    //时间服务类
    private String showTimeServiceName;
    //时间方法
    private String showTimeServiceMethod;

    //是否返回时间
    private String isAddDefineParams;
    //时间服务类
    private String addDefineServiceName;
    //时间方法
    private String addDefineServiceMethod;

    private RedisService redisServiceUtil;

    public String getDBConfigByKey(String key, String defaultValue) {
        if (StringUtils.isBlank(key) || cacheDatabaseConfiguration == null) {
            return defaultValue;
        }

        return cacheDatabaseConfiguration.getString(key, defaultValue);
    }

    public RedisService getRedisServiceUtil() {
        return redisServiceUtil;
    }

    public void setRedisServiceUtil(RedisService redisServiceUtil) {
        this.redisServiceUtil = redisServiceUtil;
    }

    public String getIsUsePerformance() {
        return isUsePerformance;
    }

    public void setIsUsePerformance(String isUsePerformance) {
        this.isUsePerformance = isUsePerformance;
    }

    public String getIsUseHttpValid() {
        return isUseHttpValid;
    }

    public void setIsUseHttpValid(String isUseHttpValid) {
        this.isUseHttpValid = isUseHttpValid;
    }

    public String getIsUseL3Valid() {
        return isUseL3Valid;
    }

    public void setIsUseL3Valid(String isUseL3Valid) {
        this.isUseL3Valid = isUseL3Valid;
    }

    public String getSsoCenterUrl() {
        return ssoCenterUrl;
    }

    public void setSsoCenterUrl(String ssoCenterUrl) {
        this.ssoCenterUrl = ssoCenterUrl;
    }

    public String getSsoCenterChannel() {
        return ssoCenterChannel;
    }

    public void setSsoCenterChannel(String ssoCenterChannel) {
        this.ssoCenterChannel = ssoCenterChannel;
    }

    public String getSsoCenterType() {
        return ssoCenterType;
    }

    public void setSsoCenterType(String ssoCenterType) {
        this.ssoCenterType = ssoCenterType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRestServicePath() {
        return restServicePath;
    }

    public void setRestServicePath(String restServicePath) {
        this.restServicePath = restServicePath;
    }

    public String getIsShowResponseTime() {
        return isShowResponseTime;
    }

    public void setIsShowResponseTime(String isShowResponseTime) {
        this.isShowResponseTime = isShowResponseTime;
    }

    public String getShowTimeServiceName() {
        return showTimeServiceName;
    }

    public void setShowTimeServiceName(String showTimeServiceName) {
        this.showTimeServiceName = showTimeServiceName;
    }

    public String getShowTimeServiceMethod() {
        return showTimeServiceMethod;
    }

    public void setShowTimeServiceMethod(String showTimeServiceMethod) {
        this.showTimeServiceMethod = showTimeServiceMethod;
    }

    public String getIsAddDefineParams() {
        return isAddDefineParams;
    }

    public void setIsAddDefineParams(String isAddDefineParams) {
        this.isAddDefineParams = isAddDefineParams;
    }

    public String getAddDefineServiceName() {
        return addDefineServiceName;
    }

    public void setAddDefineServiceName(String addDefineServiceName) {
        this.addDefineServiceName = addDefineServiceName;
    }

    public String getAddDefineServiceMethod() {
        return addDefineServiceMethod;
    }

    public void setAddDefineServiceMethod(String addDefineServiceMethod) {
        this.addDefineServiceMethod = addDefineServiceMethod;
    }

    public String getIsShowDebugLog() {
        return isShowDebugLog;
    }

    public void setIsShowDebugLog(String isShowDebugLog) {
        this.isShowDebugLog = isShowDebugLog;
    }

    public CacheDatabaseConfiguration getCacheDatabaseConfiguration() {
        return cacheDatabaseConfiguration;
    }

    public void setCacheDatabaseConfiguration(CacheDatabaseConfiguration cacheDatabaseConfiguration) {
        this.cacheDatabaseConfiguration = cacheDatabaseConfiguration;
    }

    public String getIsUsePerformanceDBKey() {
        return isUsePerformanceDBKey;
    }

    public void setIsUsePerformanceDBKey(String isUsePerformanceDBKey) {
        this.isUsePerformanceDBKey = isUsePerformanceDBKey;
    }

    public String getIsUseHttpValidDBKey() {
        return isUseHttpValidDBKey;
    }

    public void setIsUseHttpValidDBKey(String isUseHttpValidDBKey) {
        this.isUseHttpValidDBKey = isUseHttpValidDBKey;
    }

    public String getSsoCenterUrlDBKey() {
        return ssoCenterUrlDBKey;
    }

    public void setSsoCenterUrlDBKey(String ssoCenterUrlDBKey) {
        this.ssoCenterUrlDBKey = ssoCenterUrlDBKey;
    }

    public String getSsoCenterChannelDBKey() {
        return ssoCenterChannelDBKey;
    }

    public void setSsoCenterChannelDBKey(String ssoCenterChannelDBKey) {
        this.ssoCenterChannelDBKey = ssoCenterChannelDBKey;
    }

    public String getSsoCenterTypeDBKey() {
        return ssoCenterTypeDBKey;
    }

    public void setSsoCenterTypeDBKey(String ssoCenterTypeDBKey) {
        this.ssoCenterTypeDBKey = ssoCenterTypeDBKey;
    }

    public String getAppIdDBKey() {
        return appIdDBKey;
    }

    public void setAppIdDBKey(String appIdDBKey) {
        this.appIdDBKey = appIdDBKey;
    }

    public String getSecretKeyDBKey() {
        return secretKeyDBKey;
    }

    public void setSecretKeyDBKey(String secretKeyDBKey) {
        this.secretKeyDBKey = secretKeyDBKey;
    }

    public String getTokenCacheLife() {
        return tokenCacheLife;
    }

    public void setTokenCacheLife(String tokenCacheLife) {
        this.tokenCacheLife = tokenCacheLife;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        setIsUsePerformance(getDBConfigByKey(isUsePerformanceDBKey, isUsePerformance));
        setIsUseHttpValid(getDBConfigByKey(isUseHttpValidDBKey, isUseHttpValid));
        setSsoCenterUrl(getDBConfigByKey(ssoCenterUrlDBKey, ssoCenterUrl));
        setSsoCenterChannel(getDBConfigByKey(ssoCenterChannelDBKey, ssoCenterChannel));
        setSsoCenterType(getDBConfigByKey(ssoCenterTypeDBKey, ssoCenterType));
        setAppId(getDBConfigByKey(appIdDBKey, appId));
        setSecretKey(getDBConfigByKey(secretKeyDBKey, secretKey));
    }
}
