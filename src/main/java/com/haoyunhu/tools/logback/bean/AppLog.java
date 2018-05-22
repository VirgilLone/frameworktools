package com.haoyunhu.tools.logback.bean;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zg on 2016/12/8.
 */
public class AppLog implements Serializable {

    @JsonProperty("SystemAlias")
    private String systemAlias;
    @JsonProperty("Topic")
    private String topic;
    @JsonProperty("CreatedDate")
    private Date createdDate;
    @JsonProperty("Content")
    private String content;
    @JsonProperty("Ip")
    private String ip;
    @JsonProperty("Level")
    private int level;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Tag")
    private String tag;
    @JsonProperty("EventId")
    private String eventId;
    @JsonProperty("MetricValue")
    private double metricValue;

    private String createdDateString;

    public AppLog() {
        this.ip = KibanaLogSettings.Ip;
        this.systemAlias = KibanaLogSettings.systemAlias;
    }

    public String getCreatedDateString() {
        return createdDateString;
    }

    public void setCreatedDateString(String createdDateString) {
        this.createdDateString = createdDateString;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSystemAlias() {
        return this.systemAlias;
    }

    public void setSystemAlias(String systemAlias) {
        this.systemAlias = systemAlias.toLowerCase().trim();
    }


    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        if (level > 99) {
            this.level = 99;
        } else {
            this.level = level;
        }

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public double getMetricValue() {
        return this.metricValue;
    }

    public void setMetricValue(double metricValue) {
        this.metricValue = metricValue;
    }
}
