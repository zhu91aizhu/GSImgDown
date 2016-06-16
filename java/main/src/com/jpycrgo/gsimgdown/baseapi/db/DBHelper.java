package com.jpycrgo.gsimgdown.baseapi.db;

import com.jpycrgo.gsimgdown.bean.ImageBean;
import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author mengzx
 * @date 2016/5/13
 * @since 1.0.0
 */
public class DBHelper {

    private static final Logger logger = LogManager.getLogger(DBHelper.class);

    static {
        DbUtils.loadDriver("org.sqlite.JDBC");
    }

    public static boolean insertRecord(Connection conn, ImageBean bean) {
        QueryRunner runner = new QueryRunner();
        String sql = "INSERT INTO IMAGE(sid, URL) VALUES(?, ?)";
        int affectCount = 0;

        String url = bean.getUrl();
        String sid = bean.getSid();

        try {
            affectCount = runner.update(conn, sql, new Object[]{sid, url});
        }
        catch (SQLException e) {
            logger.error("插入图片数据失败，message：" + e.getMessage());
        }

        return affectCount > 0 ? true : false;
    }

    public static boolean insertRecord(Connection conn, ImageThemeBean bean) {
        QueryRunner runner = new QueryRunner();
        String sql = "INSERT INTO IMAGE_THEME(TITLE, DESCRIPTION, URL, SID) VALUES(?, ?, ?, ?)";
        int affectCount = 0;

        String title = bean.getTitle();
        if (StringUtils.isBlank(title)) {
            title = bean.getDescription();
        }

        String description = bean.getDescription();
        if (StringUtils.isBlank(description)) {
            description = title;
        }

        String url = bean.getUrl();
        String sid = bean.getSid();

        try {
            affectCount = runner.update(conn, sql, new Object[]{title, description, url, sid});
        }
        catch (SQLException e) {
            logger.error("插入图片主题数据失败，message：" + e.getMessage());
        }

        return affectCount > 0 ? true : false;
    }

}
