package com.jpycrgo.gsimgdown.baseapi.db.handler;

import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author mengzx
 * @date 2016/5/13
 * @since 1.0.0
 */
public class ImageThemeResultSetHandler implements ResultSetHandler<ImageThemeBean> {

    @Override
    public ImageThemeBean handle(ResultSet rs) throws SQLException {
        String title = rs.getString("title");
        String description = rs.getString("description");
        String url = rs.getString("url");
        String sid = rs.getString("sid");

        ImageThemeBean bean = new ImageThemeBean(title, description, url, sid);
        return bean;
    }

}
