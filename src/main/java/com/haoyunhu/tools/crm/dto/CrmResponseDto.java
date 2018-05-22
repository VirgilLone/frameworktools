package com.haoyunhu.tools.crm.dto;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by junming.qi on 2015/8/4.
 */
public class CrmResponseDto {
    //-1：错误,0：提交成功,-50：登录失效,-200：数据包丢失
    @JsonProperty("RSCode")
    private int rsCode;
    //消息提示
    @JsonProperty("Msg")
    private String msg;
    //错误信息
    @JsonProperty("Message")
    private String message;
    //用户信息
    @JsonProperty("Data")
    private CrmDataInfoDto data;

    public CrmDataInfoDto getData() {
        return data;
    }

    public void setData(CrmDataInfoDto data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRsCode() {
        return rsCode;
    }

    public void setRsCode(int rsCode) {
        this.rsCode = rsCode;
    }
}
