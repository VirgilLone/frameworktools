package com.haoyunhu.tools.crm.dto;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by junming.qi on 2015/8/4.
 */
public class CrmLoginRequestDto {
    //账号名
    @JsonProperty("Account")
    private String account = "";
    //密码
    @JsonProperty("Password")
    private String password = "";
    //渠道
    @JsonProperty("Channel")
    private String channel = "";
    //来源
    @JsonProperty("Type")
    private String type = "";

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
