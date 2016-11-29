package com.jpycrgo.gsimgdown.manager;

/**
 * @author mengzx
 * @date 2016/6/16
 * @since 1.0.2
 */
public class ImageCheckor implements Checkable {

    private String imgURL;

    public ImageCheckor(String imgURL) {
        this.imgURL = imgURL;
    }

    @Override
    public boolean check() {
        return DBManager.isExistsImage(imgURL);
    }

}
