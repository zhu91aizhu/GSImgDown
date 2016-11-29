package com.jpycrgo.gsimgdown;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jpycrgo.gsimgdown.baseapi.db.DBExecutorServiceManager;
import com.jpycrgo.gsimgdown.baseapi.net.ImageDownloader;
import com.jpycrgo.gsimgdown.baseapi.net.ImageSiteAnalyzer;
import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import com.jpycrgo.gsimgdown.manager.CheckManager;
import com.jpycrgo.gsimgdown.manager.DBManager;
import com.jpycrgo.gsimgdown.utils.AppSetting;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mengzx
 * @date 2016/4/27
 * @since 1.0.0
 */
public class Main {

    static {
        File file = new File(System.getProperty("user.dir"), "conf/log4j.xml");
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
        final long beginTime = System.currentTimeMillis();

        ImageSiteAnalyzer analyzer = new ImageSiteAnalyzer("http://www.gamersky.com/ent/wp/");
        int pageTotal = analyzer.getPageTotal();
        int beginPageIndex = 0;
        int endPageIndex = pageTotal;

        boolean enablePagination = AppSetting.isEnablePagination();
        if (enablePagination) {
            beginPageIndex = Optional.of(AppSetting.getBeginIndex()).get();
            endPageIndex = Optional.of(AppSetting.getEndIndex()).get();
        }

        if (beginPageIndex > endPageIndex) {
            Main.LOGGER.error("开始页数或结束页数参数配置有误.");
            System.exit(1);
        }

        Main.LOGGER.info("下载开始页数：" + beginPageIndex);
        Main.LOGGER.info("下载结束页数：" + endPageIndex);

        analyzer.setBeginPageIndex(beginPageIndex);
        analyzer.setEndPageIndex(endPageIndex);

        List<ImageThemeBean> imageThemeSetBeans = analyzer.analysis();
        ImageDownloader.leftImageThemeCount = new AtomicInteger(imageThemeSetBeans.size());

        CheckManager.setCheckType(AppSetting.getCheckor());
        DBManager.initDataBase();

        int threadCount = AppSetting.getDownloadThreadCount();
        Main.LOGGER.info("开启下载线程数： " + threadCount);

        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("IMAGE-DOWNLOADER-%d").build();
        final ExecutorService service = Executors.newFixedThreadPool(threadCount, threadFactory);

        List<Future<Long>> futures = Lists.newArrayList();
        imageThemeSetBeans.forEach(bean -> {
            ImageDownloadTask task = new ImageDownloadTask(bean, AppSetting.getSavePath());
            Future<Long> future = service.submit(task);
            futures.add(future);
        });
        service.shutdown();

        long fileTotalByteSize = 0L;
        for (Future<Long> future : futures) {
            fileTotalByteSize += future.get();
        }

        DBExecutorServiceManager.shutdown();
        DBExecutorServiceManager.awaitTermination();

        long runningTime = System.currentTimeMillis() - beginTime;
        Main.LOGGER.info("本次运行时间: " + runningTime + " ms");

        Main.LOGGER.info("本次共下载：" + FileUtils.byteCountToDisplaySize(fileTotalByteSize));
        Main.LOGGER.info("本次下载速度：" + getDownloadSpeed(runningTime, fileTotalByteSize));
        Main.LOGGER.info("任务执行完成，程序正在退出.");
        System.exit(0);
    }

    private static String getDownloadSpeed(long runningTime, long bytes) {
        // 计算每秒的速度
        long speed = bytes * 1000 / runningTime;
        BigInteger size = BigInteger.valueOf(speed);
        String displaySpeed = "";

        if (size.divide(FileUtils.ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySpeed = String.format("%.2f GB", size.doubleValue() / FileUtils.ONE_GB_BI.longValue());
        }
        else if (size.divide(FileUtils.ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySpeed = String.format("%.2f MB", size.doubleValue() / FileUtils.ONE_MB_BI.longValue());
        }
        else if (size.divide(FileUtils.ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySpeed = String.format("%.2f KB", size.doubleValue() / FileUtils.ONE_KB_BI.longValue());
        }
        else {
            displaySpeed = String.valueOf(size) + " byte";
        }

        return displaySpeed + "/S";
    }

}