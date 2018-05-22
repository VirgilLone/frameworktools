package com.haoyunhu.tools.rest.bean;

/**
 * Created by weijun.hu on 2015/7/3.
 * 配置存放类
 */
public class RestTestServiceInfo {
    private String name;
    private String path;
    private String value;

    public RestTestServiceInfo(String name, String path, String value) {
        this.name = name;
        this.path = path;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
