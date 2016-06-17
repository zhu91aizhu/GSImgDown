package com.jpycrgo.gsimgdown.baseapi.db;

import com.jpycrgo.gsimgdown.bean.ImageBean;
import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author mengzx
 * @date 2016/5/13
 * @since 1.0.1
 */
public class DBThreadManager {

    private static final Logger LOGGER = LogManager.getLogger(DBThreadManager.class);

    public static final BlockingQueue<AbstractRecord> RECORD_QUEUE = new LinkedBlockingQueue<AbstractRecord>();

    private static final RecordDBThread SAVE_RECORD_THREAD = new RecordDBThread();

    public static boolean MAIN_FINISHED = false;

    static {
        SAVE_RECORD_THREAD.setName("DB-Thread");
    }

    /**
     * 中断数据记录线程
     */
    public static void interruptRecordThread() {
        MAIN_FINISHED = true;
        if (RECORD_QUEUE.size() == 0) {
            SAVE_RECORD_THREAD.interrupt();
        }
    }

    /**
     * 设置主线程等待数据记录线程执行完毕
     */
    public static void joinRecordThread() {
        try {
            SAVE_RECORD_THREAD.join();
        }
        catch (InterruptedException e) {
            LOGGER.error("设置主线程等待数据记录线程失败");
        }
    }

    /**
     * 激活数据记录线程
     */
    public static void activateRecordThread() {
        if (!SAVE_RECORD_THREAD.isAlive()) {
            SAVE_RECORD_THREAD.start();
        }
    }

    /**
     * 保存数据记录
     * @param record 记录
     */
    public static void saveRecord(AbstractRecord record) {
        try {
            RECORD_QUEUE.put(record);
        }
        catch (InterruptedException e) {
            LOGGER.error(String.format("向数据线程放入%s数据失败，message: %s", record.recordName, e.getMessage()));
        }
    }

    /**
     * @since 1.0.0
     */
    @Deprecated
    public static void saveImageTheme(ImageThemeBean bean) {
        AbstractRecord record = new ImageThemeBeanRecord(bean);

        try {
            RECORD_QUEUE.put(record);
        }
        catch (InterruptedException e) {
            LOGGER.error("向图片主题数据线程放入数据失败，message: " + e.getMessage());
        }

        if (!SAVE_RECORD_THREAD.isAlive()) {
            SAVE_RECORD_THREAD.start();
        }
    }

    /**
     * @since 1.0.1
     */
    @Deprecated
    public static void saveImage(ImageBean bean) {
        AbstractRecord record = new ImageBeanRecord(bean);

        try {
            RECORD_QUEUE.put(record);
        }
        catch (InterruptedException e) {
            LOGGER.error("向图片主题数据线程放入数据失败，message: " + e.getMessage());
        }

        if (!SAVE_RECORD_THREAD.isAlive()) {
            SAVE_RECORD_THREAD.start();
        }
    }

    /**
     * @since 1.0.0
     */
    @Deprecated
    public static void saveImage(String sid, String url) {
        saveImage(new ImageBean(sid, url));
    }

}
