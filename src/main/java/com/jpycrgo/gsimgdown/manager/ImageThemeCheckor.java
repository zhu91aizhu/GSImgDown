package com.jpycrgo.gsimgdown.manager;

/**
 * @author mengzx
 * @date 2016/6/16
 * @since 1.0.2
 */
public class ImageThemeCheckor implements Checkable {

    private String sid;

    public ImageThemeCheckor(String sid) {
        this.sid = sid;
    }

    @Override
    public boolean check() {
        return DBManager.isExistsImageTheme(sid);
    }

}
