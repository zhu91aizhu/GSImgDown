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
 * @since 1.0.0
 */
public class DBThreadManager {

    private static final Logger LOGGER = LogManager.getLogger(DBThreadManager.class);

    public static final BlockingQueue<Object> IMAGE_THEME_QUEUE = new LinkedBlockingQueue<Object>();

    private static final ImageThemeDBThread IMAGE_THEME_THREAD = new ImageThemeDBThread();

    static {
        IMAGE_THEME_THREAD.setName("DB-Thread");
    }

    public static void saveImageThemeBean(ImageThemeBean bean) {
        try {
            IMAGE_THEME_QUEUE.put(bean);
        }
        catch (InterruptedException e) {
            LOGGER.error("向图片主题数据线程放入数据失败，message: " + e.getMessage());
        }

        if (!IMAGE_THEME_THREAD.isAlive()) {
            IMAGE_THEME_THREAD.start();
        }
    }

    public static void saveImage(String sid, String url) {
        try {
            IMAGE_THEME_QUEUE.put(new ImageBean(sid, url));
        }
        catch (InterruptedException e) {
            LOGGER.error("向图片主题数据线程放入数据失败，message: " + e.getMessage());
        }

        if (!IMAGE_THEME_THREAD.isAlive()) {
            IMAGE_THEME_THREAD.start();
        }
    }

}
