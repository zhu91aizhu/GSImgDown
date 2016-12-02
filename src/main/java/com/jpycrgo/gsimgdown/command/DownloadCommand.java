package com.jpycrgo.gsimgdown.command;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jpycrgo.gsimgdown.ImageDownloadTask;
import com.jpycrgo.gsimgdown.baseapi.db.DBExecutorServiceManager;
import com.jpycrgo.gsimgdown.baseapi.net.ImageDownloader;
import com.jpycrgo.gsimgdown.baseapi.net.ImageSiteAnalyzer;
import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import com.jpycrgo.gsimgdown.manager.CheckManager;
import com.jpycrgo.gsimgdown.manager.DBManager;
import com.jpycrgo.gsimgdown.utils.AppSetting;
import io.airlift.airline.Command;
import io.airlift.airline.Option;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mengzx
 * @date 2016/11/29
 * @since 1.2.0
 */
@Command(name = "down", description = "壁纸下载")
public class DownloadCommand implements GSCommand {

    private static final Logger LOGGER = LogManager.getLogger("applog");

    @Option(name = {"-b", "--begin"}, description = "开始页码")
    public int beginIndexOpt = -1;

    @Option(name = {"-e", "--end"}, description = "结束页码")
    public int endIndexOpt = -1;

    @Option(name = {"-t", "--thread"}, description = "线程数量")
    public int threadCountOpt = -1;

    @Option(name = {"-c", "--check"}, description = "检查类型", allowedValues = {"NO", "FILE", "IMAGE", "IMAGE_THEME"})
    public String checkType;

    @Override
    public void execute() throws Exception {
        final long beginTime = System.currentTimeMillis();

        ImageSiteAnalyzer analyzer = new ImageSiteAnalyzer("http://www.gamersky.com/ent/wp/");
        int pageTotal = analyzer.getPageTotal();
        int beginPageIndex = 0;
        int endPageIndex = pageTotal;

        boolean enablePagination = AppSetting.isEnablePagination();
        if (beginIndexOpt != -1) {
            beginPageIndex = beginIndexOpt;
        }
        else {
            if (enablePagination) {
                beginPageIndex = Optional.of(AppSetting.getBeginIndex()).get();
            }
        }

        if (endIndexOpt != -1) {
            endPageIndex = endIndexOpt;
        }
        else {
            if (enablePagination) {
                endPageIndex = Optional.of(AppSetting.getEndIndex()).get();
            }
        }

        if (beginPageIndex > endPageIndex) {
            DownloadCommand.LOGGER.error("开始页数或结束页数参数配置有误.");
            return;
        }

        DownloadCommand.LOGGER.info("下载开始页数：" + beginPageIndex);
        DownloadCommand.LOGGER.info("下载结束页数：" + endPageIndex);

        analyzer.setBeginPageIndex(beginPageIndex);
        analyzer.setEndPageIndex(endPageIndex);

        List<ImageThemeBean> imageThemeSetBeans = analyzer.analysis();
        ImageDownloader.leftImageThemeCount = new AtomicInteger(imageThemeSetBeans.size());

        initCheckType();
        DBManager.initDataBase();

        int threadCount = initThreadCount();
        DownloadCommand.LOGGER.info("开启下载线程数： " + threadCount);

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
        DownloadCommand.LOGGER.info("本次运行时间: " + runningTime + " ms");

        DownloadCommand.LOGGER.info("本次共下载：" + FileUtils.byteCountToDisplaySize(fileTotalByteSize));
        DownloadCommand.LOGGER.info("本次下载速度：" + getDownloadSpeed(runningTime, fileTotalByteSize));
        DownloadCommand.LOGGER.info("任务执行完成，程序正在退出.");
    }

    private int initThreadCount() {
        int threadCount = AppSetting.getDownloadThreadCount();
        if (threadCountOpt != -1) {
            threadCount = threadCountOpt;
        }
        return threadCount;
    }

    private void initCheckType() {
        if (checkType != null) {
            CheckManager.setCheckType(checkType);
        }
        else {
            CheckManager.setCheckType(AppSetting.getCheckor());
        }
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
