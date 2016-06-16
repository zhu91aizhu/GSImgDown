package com.jpycrgo.gsimgdown;

import com.jpycrgo.gsimgdown.baseapi.db.AbstractBeanRecord;
import com.jpycrgo.gsimgdown.baseapi.db.DBThreadManager;
import com.jpycrgo.gsimgdown.baseapi.db.ImageThemeBeanRecord;
import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author mengzx
 * @date 2016/4/29
 * @since 1.0.1
 */
public class ImageDownloadTask implements Runnable {

    private BlockingQueue<ImageThemeBean> imageThemeQueue;

    private String path;

    private static final Logger logger = LogManager.getLogger(ImageDownloadTask.class);

    public ImageDownloadTask(BlockingQueue<ImageThemeBean> imageThemeQueue, String path) throws IOException {
        this.imageThemeQueue = imageThemeQueue;
        this.path = path;
    }

    private List<String> getImageUrls(ImageThemeBean bean)throws IOException {
        List<String> pageUrls = getPageUrls(bean);
        List<String> imageurls = new ArrayList<>(pageUrls.size() * 4);
        for (String url : pageUrls) {
            List<String> urls = getImageUrls(url);
            urls.forEach(x -> logger.debug(String.format("图片主题: [%s] 包含: [%s - %s]", bean.getTitle(), url, x)));
            imageurls.addAll(urls);
        }

        return imageurls;
    }

    private List<String> getPageUrls(ImageThemeBean bean) throws IOException {
        Document document = DocumentUtils.getUrlDocument(bean.getUrl());
        Elements elements = document.body().select("div[class='page_css'] a");
        List<String> pageUrls = new ArrayList<>(elements.size());
        for (Element e : elements) {
            pageUrls.add(e.attr("href"));
        }
        pageUrls.remove(pageUrls.size() - 1);
        logger.debug(String.format("图片主题 [%s] 共有: %d页", bean.getTitle(), pageUrls.size()));

        return pageUrls;
    }

    @Override
    public void run() {
        while (true) {
            ImageThemeBean imageThemeBean = imageThemeQueue.poll();
            if (imageThemeBean == null) {
                break;
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

            List<String> imageUrls = Collections.EMPTY_LIST;
            try {
                imageUrls = getImageUrls(imageThemeBean);
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
            DBThreadManager.saveRecord(record);

            int count = ImageDownloader.leftImageThemeCount.getAndDecrement();
            logger.info(String.format("剩余未下载图片主题数: [%d]", count - 1));
        }

        logger.info(String.format("Thread: %s 退出.", Thread.currentThread().getName()));
    }

    private List<String> getImageUrls(String url) throws IOException {
        Document document = DocumentUtils.getUrlDocument(url);
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
