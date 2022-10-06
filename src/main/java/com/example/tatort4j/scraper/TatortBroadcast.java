package com.example.tatort4j.scraper;

import java.io.Serializable;

// Data Object for single broadcast of Tatort episode
public class TatortBroadcast implements Serializable {
    private String title = "";
    private String city = "";
    private String inspectors = "";
    private String time = "";
    private String channel = "";
    private String url = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInspectors() {
        return inspectors;
    }

    public void setInspectors(String inspectors) {
        this.inspectors = inspectors;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (!url.startsWith("https://www.daserste.de")) {
                url = "https://www.daserste.de" + url;
            }
        this.url = url;
    }
}
