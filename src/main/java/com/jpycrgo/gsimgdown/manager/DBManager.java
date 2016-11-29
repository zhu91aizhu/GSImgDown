package com.jpycrgo.gsimgdown.manager;

import com.jpycrgo.gsimgdown.baseapi.db.DBHelper;
import com.jpycrgo.gsimgdown.utils.AppSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author mengzx
 * @date 2016/6/16
 * @since 1.0.2
 */
public class DBManager {

    private static Connection CONN = null;

    private static final Logger LOGGER = LogManager.getLogger(DBManager.class);

    static {
        DBHelper.loadDriverAndInitDBPath();

        if (CONN == null) {
            try {
                CONN = DriverManager.getConnection(AppSetting.getDatabaseURL());
            }
            catch (SQLException e) {
                LOGGER.error("获取连接失败.");
            }
        }
    }

    public static boolean isExistsImage(String imgURL) {
        return DBHelper.isExistsImage(CONN, imgURL);
    }

    public static boolean isExistsImageTheme(String sid) {
        return DBHelper.isExistsImageTheme(CONN, sid);
    }

    public static void initDataBase() {
        DBHelper.initDataBase(CONN);
    }
}
