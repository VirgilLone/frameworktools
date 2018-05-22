package com.haoyunhu.tools.rest.bean;

import java.util.Arrays;
import java.util.List;

/**
 * Created by weijun.hu on 2015/7/3.
 * 配置存放类
 */
public class RestServiceInfo {
    private String id;
    private String serviceName;
    private String serviceMethod;
    private List<String> levels;
    //true:打开幂等 false:或者null关闭幂等
    private String idempotent;
    //幂等时间(秒)
    private String idempotentTimes;

    public RestServiceInfo() {

    }

    public RestServiceInfo(String id, String serviceName, String serviceMethod, String[] levels, String idempotent, String idempotentTimes) {
        this.id = id;
        this.serviceName = serviceName;
        this.serviceMethod = serviceMethod;
        this.levels = Arrays.asList(levels);
        this.idempotent = idempotent;
        this.idempotentTimes = idempotentTimes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public List<String> getLevels() {
        return levels;
    }

    public void setLevels(List<String> levels) {
        this.levels = levels;
    }

    public String getIdempotent() {
        return idempotent;
    }

    public void setIdempotent(String idempotent) {
        this.idempotent = idempotent;
    }

    public String getIdempotentTimes() {
        return idempotentTimes;
    }

    public void setIdempotentTimes(String idempotentTimes) {
        this.idempotentTimes = idempotentTimes;
    }
}
