package com.haoyunhu.tools.crm.dto;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by junming.qi on 2015/8/4.
 */
public class CrmUserDataInfoDto {
    //用户ID（必须）
    @JsonProperty("UserId")
    private long userId = 0;
    //姓名
    @JsonProperty("RealName")
    private String realName = "";
    //手机
    @JsonProperty("Mobile")
    private String mobile = "";
    //邮箱
    @JsonProperty("Email")
    private String email = "";
    //QQ
    @JsonProperty("QQ ")
    private String qq = "";
    //客户名称
    @JsonProperty("CustomerName")
    private String customerName = "";

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
