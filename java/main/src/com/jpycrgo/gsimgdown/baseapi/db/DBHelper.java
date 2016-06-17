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

    /**
     * 查询图片是否存在
     * @since 1.0.2
     */
    public static boolean isExistsImage(Connection conn, String imgURL) {
        QueryRunner runner = new QueryRunner();
        String sql = "SELECT COUNT(IMAGEID) AS IMAGE_COUNT FROM IMAGE WHERE URL=?";
        Object[] params = new Object[]{imgURL};
        Boolean exists = false;

        try {
            exists = runner.query(conn, sql, rs -> {
                int count = 0;
                if (rs.next()) {
                    count = rs.getInt("IMAGE_COUNT");
                }

                return count > 0 ? true : false;
            }, params);
        }
        catch (SQLException e) {
            logger.error(String.format("查询 [%s] 记录失败，message： %s", imgURL, e.getMessage()));
        }

        return exists;
    }

    /**
     * 查询图片主题是否存在
     * @since 1.0.2
     */
    public static boolean isExistsImageTheme(Connection conn, String sid) {
        QueryRunner runner = new QueryRunner();
        String sql = "SELECT COUNT(SID) AS SID_COUNT FROM IMAGE_THEME WHERE SID=?";
        Object[] params = new Object[]{sid};
        boolean exists = false;

        try {
            exists = runner.query(conn, sql, rs -> {
                int count = 0;
                if (rs.next()) {
                    count = rs.getInt("SID_COUNT");
                }

                return count > 0 ? true : false;
            }, params);
        }
        catch (SQLException e) {
            logger.error(String.format("查询 [%s] 记录失败，message： %s", sid, e.getMessage()));
        }

        return exists;
    }

    /**
     * @since 1.0.1
     */
    public static boolean insertRecord(Connection conn, AbstractRecord record) {
        QueryRunner runner = new QueryRunner();
        String sql = record.generateSQL();
        Object[] params = record.getParams();
        int affectCount = 0;

        try {
            affectCount = runner.update(conn, sql, params);
        }
        catch (SQLException e) {
            logger.error(String.format("插入%s失败，message： %s", record.getRecordName(), e.getMessage()));
        }

        return affectCount > 0 ? true : false;
    }

    /**
     * @since 1.0.0
     */
    @Deprecated
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

    /**
     * @since 1.0.0
     */
    @Deprecated
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
