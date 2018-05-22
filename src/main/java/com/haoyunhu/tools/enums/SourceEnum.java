package com.haoyunhu.tools.enums;

//物流相关的应用枚举
public enum SourceEnum {
    TMS(1, "物流TMS"),
    CARRIER_APP(2, "承运商APP"),
    ENTRUST_APP(3, "委托方APP"),
    WEIXIN(5, "微信"),
    CARRIER_WEB(10, "承运商用户中心"),
    ENTRUST_WEB(11, "物流推广"),
    TMS_APP(13, "移动端TMS"),
    CRM(14, "物流CRM"),
    SCHEDULE(21, "物流定时器"),
    TRUCK_CENTER(23, "专车管理系统"),
    ;

    private int key;

    private String value;

    SourceEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
