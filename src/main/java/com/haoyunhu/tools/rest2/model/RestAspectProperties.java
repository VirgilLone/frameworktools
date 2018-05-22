package com.haoyunhu.tools.rest2.model;

/**
 * Created by weijun.hu on 2015/11/24.
 */
public class RestAspectProperties {

    //时间服务类
    private Object showTimeService;
    //时间方法
    private String showTimeServiceMethod;
    //接口标示
    private String appId;
    //接口密钥
    private String secretKey;
    //SSO请求地址
    private String ssoCenterUrl;
    //SSO请求项目
    private String ssoCenterChannel;
    //SSO请求类型
    private String ssoCenterType;
    //是否引入配置到测试页面
    private String isImportToTest = "1";

    public Object getShowTimeService() {
        return showTimeService;
    }

    public void setShowTimeService(Object showTimeService) {
        this.showTimeService = showTimeService;
    }

    public String getShowTimeServiceMethod() {
        return showTimeServiceMethod;
    }

    public void setShowTimeServiceMethod(String showTimeServiceMethod) {
        this.showTimeServiceMethod = showTimeServiceMethod;
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

    public String getIsImportToTest() {
        return isImportToTest;
    }

    public void setIsImportToTest(String isImportToTest) {
        this.isImportToTest = isImportToTest;
    }
}
