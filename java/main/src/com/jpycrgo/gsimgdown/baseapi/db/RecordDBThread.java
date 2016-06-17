package com.jpycrgo.gsimgdown.baseapi.db;

import com.jpycrgo.gsimgdown.utils.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author mengzx
 * @date 2016/5/13
 * @since 1.0.1
 */
public class RecordDBThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(RecordDBThread.class);

    private static Connection conn = null;

    @Override
    public void run() {
        initConnection();

        while (!Thread.interrupted()) {
            if (DBThreadManager.MAIN_FINISHED && DBThreadManager.RECORD_QUEUE.size() == 0) {
                LOGGER.info("主线程执行完成，并且数据记录队列为空，数据记录线程退出");
                break;
            }

            try {
                AbstractRecord record = DBThreadManager.RECORD_QUEUE.take();

                boolean success = DBHelper.insertRecord(conn, record);
                if (success) {
                    LOGGER.info(String.format("向数据库中插入 [%s] 成功", record.getRecordName()));
                }
                else {
                    LOGGER.info(String.format("向数据库中插入 [%s] 失败", record.getRecordName()));
                }
            }
            catch (InterruptedException e) {
                if (StringUtils.isNotBlank(e.getMessage())) {
                    LOGGER.error("主程序退出或者从记录数据队列中获取数据失败, message: " + e.getMessage());
                }
                else {
                    LOGGER.info("主线程执行完成，并且数据记录队列为空，数据记录线程退出");
                    break;
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
