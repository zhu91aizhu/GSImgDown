package com.jpycrgo.gsimgdown.baseapi.db;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jpycrgo.gsimgdown.baseapi.db.bean.AbstractRecord;
import com.jpycrgo.gsimgdown.utils.AppSetting;
import com.jpycrgo.gsimgdown.utils.ExecutorServiceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * @author mengzx
 * @date 2016/5/13
 * @since 1.0.1
 */
public class DBExecutorServiceManager {

    private static final Logger LOGGER = LogManager.getLogger(DBExecutorServiceManager.class);

    private static ExecutorService service;

    private static Connection CONN;

    static {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("DB-Thread").build();
        service = Executors.newSingleThreadExecutor(threadFactory);
    }

    /**
     * 保存数据记录
     * @param record 记录
     */
    public static void saveRecord(AbstractRecord record) {
        if (record == null) {
            return;
        }

        SaveRecordDBTask task = new SaveRecordDBTask(record);
        service.submit(task);
    }

    public static Connection getConnection() {
        if (CONN == null) {
            try {
                CONN = DriverManager.getConnection(AppSetting.getDatabaseURL());
            }
            catch (SQLException e) {
                LOGGER.error("获取连接失败.");
            }
        }

        return CONN;
    }

    public static void shutdown() {
        service.shutdown();
    }

    public static void awaitTermination() throws InterruptedException {
        ExecutorServiceUtils.awaitTermination(service);
    }

}
