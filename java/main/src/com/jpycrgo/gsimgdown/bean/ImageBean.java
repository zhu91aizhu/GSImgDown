package com.jpycrgo.gsimgdown.bean;

/**
 * @author mengzx
 * @date 2016/6/1
 * @since 1.0.0
 */
public class ImageBean {

    private int imageID;
    private String sid;
    private String url;

    public ImageBean(String sid, String url) {
        this.sid = sid;
        this.url = url;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
