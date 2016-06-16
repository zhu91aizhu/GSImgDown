package com.jpycrgo.gsimgdown.baseapi.db;

import com.jpycrgo.gsimgdown.bean.ImageBean;
import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import com.jpycrgo.gsimgdown.utils.PropertiesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author mengzx
 * @date 2016/5/13
 * @since 1.0.0
 */
public class ImageThemeDBThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(ImageThemeDBThread.class);

    private static Connection conn = null;

    @Override
    public void run() {
        initConnection();

        while (!Thread.interrupted()) {
            Object bean = null;
            try {
                bean = DBThreadManager.IMAGE_THEME_QUEUE.take();
            }
            catch (InterruptedException e) {
                LOGGER.error("从图片主题数据队列中获取数据失败, message: " + e.getMessage());
            }

            boolean success = false;
            if (bean instanceof ImageThemeBean) {
                success = DBHelper.insertRecord(conn, (ImageThemeBean) bean);
                if (success) {
                    LOGGER.info("向图片主题数据库中插入数据成功");
                }
                else {
                    LOGGER.info("向图片主题数据库中插入数据失败");
                }
            }
            else if (bean instanceof ImageBean) {
                success = DBHelper.insertRecord(conn, (ImageBean) bean);
                if (success) {
                    LOGGER.info("向图片数据库中插入数据成功");
                }
                else {
                    LOGGER.info("向图片数据库中插入数据失败");
                }
            }
        }
    }

    private void initConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(PropertiesUtils.getProperty("database_url"));
            }
            catch (SQLException e) {
                LOGGER.error("获取连接失败.");
            }
        }
    }

}
