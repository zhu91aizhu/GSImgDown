package com.jpycrgo.gsimgdown.bean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * @author mengzx
 * @date 2016/4/28
 * @since 1.0.0
 */
public class ImageThemeBean {

    private String title = StringUtils.EMPTY;
    private String description = StringUtils.EMPTY;
    private String url = StringUtils.EMPTY;
    private String sid = StringUtils.EMPTY;
    private Date time;

    public ImageThemeBean(String title, String description, String url, String sid) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.sid = sid;
    }

    public ImageThemeBean(String title, String description, String url, String sid, Date time) {
        this(title, description, url, sid);
        this.time = time;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
