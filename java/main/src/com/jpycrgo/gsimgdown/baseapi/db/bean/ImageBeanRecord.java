package com.jpycrgo.gsimgdown.baseapi.db.bean;

import com.jpycrgo.gsimgdown.bean.ImageBean;

/**
 * @author mengzx
 * @date 2016/6/16
 * @since 1.0.1
 */
public class ImageBeanRecord extends AbstractBeanRecord {

    private ImageBean imageBean;

    public ImageBeanRecord(ImageBean imgBean) {
        imageBean = imgBean;
        recordName = "图片记录";
    }

    /**
     * 生成 SQL 语句
     */
    @Override
    public String generateSQL() {
        return "INSERT INTO IMAGE(sid, URL) VALUES(?, ?)";
    }

    /**
     * 获取 SQL 语句参数
     * @return SQL 语句参数数组
     */
    @Override
    public Object[] getParams() {
        Object[] params = new Object[2];

        params[0] = imageBean.getSid();
        params[1] = imageBean.getUrl();

        return params;
    }
}
