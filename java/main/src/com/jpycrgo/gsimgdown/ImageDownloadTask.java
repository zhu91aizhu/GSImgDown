package com.jpycrgo.gsimgdown;

import com.google.common.collect.Lists;
import com.jpycrgo.gsimgdown.baseapi.db.DBExecutorServiceManager;
import com.jpycrgo.gsimgdown.baseapi.db.bean.AbstractBeanRecord;
import com.jpycrgo.gsimgdown.baseapi.db.bean.ImageThemeBeanRecord;
import com.jpycrgo.gsimgdown.baseapi.net.ImageDownloader;
import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import com.jpycrgo.gsimgdown.manager.CheckManager;
import com.jpycrgo.gsimgdown.manager.Checkable;
import com.jpycrgo.gsimgdown.utils.DocumentUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mengzx
 * @date 2016/4/29
 * @since 1.0.0
 */
public class ImageDownloadTask implements Runnable {

    private ImageThemeBean imageThemeBean;

    private String path;

    private static final Logger logger = LogManager.getLogger(ImageDownloadTask.class);

    public ImageDownloadTask(ImageThemeBean imageThemeBean, String path) {
        this.imageThemeBean = imageThemeBean;
        this.path = path;
    }

    private List<String> getImageUrls()throws IOException {
        List<String> pageUrls = getPageUrls();
        List<String> imageurls = new ArrayList<>(pageUrls.size() * 4);
        pageUrls.forEach(url -> {
            List<String> urls = getImageUrls(url);
            urls.forEach(x -> logger.debug(String.format("图片主题: [%s] 包含: [%s - %s]", imageThemeBean.getTitle(), url, x)));
            imageurls.addAll(urls);
        });

        return imageurls;
    }

    private List<String> getPageUrls() throws IOException {
        Document document = DocumentUtils.getUrlDocument(imageThemeBean.getUrl());
        Elements elements = document.body().select("div[class='page_css'] a");
        List<String> pageUrls = new ArrayList<>(elements.size());
        for (Element e : elements) {
            pageUrls.add(e.attr("href"));
        }
        pageUrls.remove(pageUrls.size() - 1);
        logger.debug(String.format("图片主题 [%s] 共有: %d页", imageThemeBean.getTitle(), pageUrls.size()));

        return pageUrls;
    }

    @Override
    public void run() {
        if (CheckManager.check(imageThemeBean.getSid(), CheckManager.CheckType.IMAGETHEME)) {
            logger.info(String.format("图片主题 [%s] 已存在，不进行下载.", imageThemeBean.getTitle()));
            int count = ImageDownloader.leftImageThemeCount.getAndDecrement();
            logger.info(String.format("剩余未下载图片主题数: [%d]", count - 1));
            return;
        }

        String path = StringUtils.EMPTY;
        if (path.endsWith(File.separator)) {
            path = this.path + imageThemeBean.getTitle();
        }
        else {
            path = this.path + File.separator + imageThemeBean.getTitle();
        }

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        List<String> imageUrls = Lists.newArrayList();
        try {
            imageUrls = getImageUrls();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        ImageDownloader downloader = new ImageDownloader(path, imageUrls, imageThemeBean.getTitle());
        downloader.setSid(imageThemeBean.getSid());

        logger.info(String.format("下载器 [%s] 开始下载.", downloader.getName()));
        downloader.download();
        logger.info(String.format("下载器 [%s] 结束下载.", downloader.getName()));

        AbstractBeanRecord record = new ImageThemeBeanRecord(imageThemeBean);
        DBExecutorServiceManager.saveRecord(record);

        int count = ImageDownloader.leftImageThemeCount.getAndDecrement();
        logger.info(String.format("剩余未下载图片主题数: [%d]", count - 1));
    }

    private List<String> getImageUrls(String url) {
        Document document = null;
        try {
            document = DocumentUtils.getUrlDocument(url);
        }
        catch (IOException e) {
            logger.error(String.format("请求 [%s] 失败. %s", url, e.getMessage()));
            return Lists.newArrayList();
        }

        Elements elements = document.body().select("div[class='Mid2L_con']>p[align='center']>a");
        List<String> urls = new ArrayList<>(elements.size());
        for (Element element : elements) {
            String href = element.attr("href");
            int pos = href.indexOf("?");
            href = href.substring(pos + 1);
            urls.add(href);
        }

        return urls;
    }

}
