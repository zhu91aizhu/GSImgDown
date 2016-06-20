package com.jpycrgo.gsimgdown.baseapi.db;

import com.jpycrgo.gsimgdown.utils.PropertiesUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author mengzx
 * @date 2016/5/13
 * @since 1.0.0
 */
public class DBHelper {

    private static final Logger logger = LogManager.getLogger(DBHelper.class);

    /**
     * 加载数据库驱动，并且创建数据库文件所在目录
     */
    public static void loadDriverAndInitDBPath() {
        DbUtils.loadDriver("org.sqlite.JDBC");

        String dbURL = PropertiesUtils.getProperty("database_url");
        int lastIndex = dbURL.lastIndexOf(":");
        String dbFilePath = dbURL.substring(lastIndex + 1);
        File dbFile = new File(dbFilePath);
        File parentFile = dbFile.getAbsoluteFile().getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
    }

    /**
     * 初始化数据库，如果数据库文件不存在则创建
     */
    public static void initDataBase(Connection conn) {
        QueryRunner runner = new QueryRunner();
        File scriptFiles = new File("scripts/");
        File[] sqlFiles = scriptFiles.listFiles((dir, name) -> {
            if (StringUtils.endsWithIgnoreCase(name, "sql")) {
                return true;
            }

            return false;
        });

        for (File sqlFile : sqlFiles) {
            try {
                StringBuilder imgSQLSB = new StringBuilder();
                LineNumberReader reader = new LineNumberReader(new FileReader(sqlFile));
                reader.lines().forEach(line -> imgSQLSB.append(line).append(System.getProperty("line.separator")));
                runner.update(conn, imgSQLSB.toString());
            }
            catch (FileNotFoundException e) {
                logger.error("没有找到数据库初始化文件, 错误信息： " + e.getMessage());
            }
            catch (SQLException e) {
                logger.error("初始化数据库语句错误.");
            }
        }
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

}
