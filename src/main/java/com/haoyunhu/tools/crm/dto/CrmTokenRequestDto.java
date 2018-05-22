package com.haoyunhu.tools.crm.dto;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by junming.qi on 2015/8/4.
 */
public class CrmTokenRequestDto {
    @JsonProperty("Token")
    private String token = "";

    public CrmTokenRequestDto(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
