package com.haoyunhu.tools.crm.dto;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

/**
 * Created by junming.qi on 2015/8/4.
 */
public class CrmDataInfoDto {
    //-1：用户名或密码不正确,0：提交成功,-3：需要强制升级,-99：账号暂时锁定,-100：禁止登录,100：SK过期
    @JsonProperty("LoginStatus")
    private String loginStatus;
    //token
    @JsonProperty("Token")
    private String token;
    //SK
    @JsonProperty("SK")
    private String sk;
    //UseId
    @JsonProperty("UseId")
    private String useId;
    //UserData
    @JsonProperty("UserData")
    private CrmUserDataInfoDto userData;
    //Channel
    @JsonProperty("Channel")
    private String channel;
    //Type
    @JsonProperty("Type")
    private String type;
    //ExtendInfo
    @JsonProperty("ExtendInfo")
    private Map extendInfo;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Map getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(Map extendInfo) {
        this.extendInfo = extendInfo;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUseId() {
        return useId;
    }

    public void setUseId(String useId) {
        this.useId = useId;
    }

    public CrmUserDataInfoDto getUserData() {
        return userData;
    }

    public void setUserData(CrmUserDataInfoDto userData) {
        this.userData = userData;
    }
}
