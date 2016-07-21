package com.jpycrgo.gsimgdown.baseapi.db.bean;

import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import org.apache.commons.lang3.StringUtils;

/**
 * @author mengzx
 * @date 2016/6/16
 * @since 1.0.1
 */
public class ImageThemeBeanRecord extends AbstractBeanRecord {

    private ImageThemeBean bean;

    public ImageThemeBeanRecord(ImageThemeBean bean) {
        this.bean = bean;
        recordName = "图片主题记录";
    }

    @Override
    public String generateSQL() {
        return "INSERT INTO IMAGE_THEME(TITLE, DESCRIPTION, URL, SID) VALUES(?, ?, ?, ?)";
    }

    @Override
    public Object[] getParams() {
        Object[] params = new Object[4];

        String title = bean.getTitle();
        if (StringUtils.isBlank(title)) {
            title = bean.getDescription();
        }


        String description = bean.getDescription();
        if (StringUtils.isBlank(description)) {
            description = title;
        }

        params[0] = title;
        params[1] = description;
        params[2] = bean.getUrl();
        params[3] = bean.getSid();

        return params;
    }

}
