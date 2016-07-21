package com.jpycrgo.gsimgdown;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jpycrgo.gsimgdown.baseapi.db.DBThreadManager;
import com.jpycrgo.gsimgdown.baseapi.net.ImageDownloader;
import com.jpycrgo.gsimgdown.baseapi.net.ImageSiteAnalyzer;
import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import com.jpycrgo.gsimgdown.manager.CheckManager;
import com.jpycrgo.gsimgdown.manager.DBManager;
import com.jpycrgo.gsimgdown.utils.ConstantsUtils;
import com.jpycrgo.gsimgdown.utils.PropertiesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author mengzx
 * @date 2016/4/27
 * @since 1.0.0
 */
public class Main {

    static {
        File file = new File("conf/log4j.xml");
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            final ConfigurationSource source = new ConfigurationSource(in);
            Configurator.initialize(null, source);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        ImageSiteAnalyzer analyzer = new ImageSiteAnalyzer("http://www.gamersky.com/ent/wp/");
        int pageTotal = analyzer.getPageTotal();
        int beginPageIndex = PropertiesUtils.getIntProperty("begin-page-index");
        int endPageIndex = PropertiesUtils.getIntProperty("end-page-index", pageTotal);
        if (beginPageIndex > endPageIndex) {
            Main.LOGGER.error("开始页数或结束页数参数配置有误.");
            System.exit(1);
        }

        Main.LOGGER.info("下载开始页数：" + beginPageIndex);
        Main.LOGGER.info("下载结束页数：" + endPageIndex);

        analyzer.setBeginPageIndex(beginPageIndex);
        analyzer.setEndPageIndex(endPageIndex);

        BlockingQueue<ImageThemeBean> imageThemeSetBeans = analyzer.analysis();
        ImageDownloader.leftImageThemeCount = new AtomicInteger(imageThemeSetBeans.size());

        CheckManager.setCheckType(PropertiesUtils.getProperty("check-type", ConstantsUtils.DEFAULT_CHECK_TYPE));
        DBManager.initDataBase();
        DBThreadManager.activateRecordThread();

        int threadCount = PropertiesUtils.getIntProperty("download-thread-count", ConstantsUtils.DEFAULT_DOWNLOADTHREAD_COUNT);
        Main.LOGGER.info("开启下载线程数： " + threadCount);

        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("IMAGE-DOWNLOADER-%d").build();
        final ExecutorService service = Executors.newFixedThreadPool(threadCount, threadFactory);
        ImageDownloadTask task = new ImageDownloadTask(imageThemeSetBeans, PropertiesUtils.getProperty("save_img_path"));
        IntStream.range(0, threadCount).parallel().forEach(i -> service.submit(task));
        service.shutdown();

        ImageDownloadThreadPool pool = new ImageDownloadThreadPool(service);
        pool.awaitTermination();

        DBThreadManager.interruptRecordThread();
        DBThreadManager.joinRecordThread();

        Main.LOGGER.info("任务执行完成，程序正在退出.");
        System.exit(0);
    }

}