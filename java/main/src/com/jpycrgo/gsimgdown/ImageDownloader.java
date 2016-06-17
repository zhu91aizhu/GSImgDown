package com.jpycrgo.gsimgdown;

import com.jpycrgo.gsimgdown.baseapi.db.AbstractRecord;
import com.jpycrgo.gsimgdown.baseapi.db.DBThreadManager;
import com.jpycrgo.gsimgdown.baseapi.db.ImageBeanRecord;
import com.jpycrgo.gsimgdown.bean.ImageBean;
import com.jpycrgo.gsimgdown.manager.CheckManager;
import com.jpycrgo.gsimgdown.manager.Checkable;
import com.jpycrgo.gsimgdown.utils.HttpClientUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mengzx
 * @date 2016/4/29
 * @since 1.0.0
 */
public class ImageDownloader {

    private String path;

    private List<String> imageurls = new ArrayList<>();

    private String name = StringUtils.EMPTY;

    public static AtomicInteger leftImageThemeCount;

    private String sid = StringUtils.EMPTY;

    private static final Logger logger = LogManager.getLogger(ImageDownloader.class);

    public ImageDownloader(String path, List<String> imageurls, String name) {
        this.path = path;
        this.imageurls = imageurls;
        this.name = name;
    }

    public void download() {
        int size = imageurls.size();
        for (int i=0; i<size; i++) {
            String url = imageurls.get(i);
            Checkable checkor = CheckManager.generateCheckor(url, CheckManager.CheckType.IMAGE);
            if (CheckManager.check(checkor)) {
                logger.info(String.format("图片 [%s] 已存在，不进行下载.", url));
                continue;
            }

            int pos = url.lastIndexOf("/");
            String filename;
            if (path.endsWith(File.separator)) {
                filename = path + url.substring(pos + 1);
            }
            else {
                filename = path + File.separator + url.substring(pos + 1);
            }

            checkor = CheckManager.generateCheckor(filename, CheckManager.CheckType.FILE);
            if (CheckManager.check(checkor)) {
                int index = filename.lastIndexOf(File.separator);
                logger.info(String.format("图片 [%s] 已存在，不进行下载.", filename.substring(index + 1)));
                continue;
            }

            // 创建Get方法实例
            HttpGet httpGet = null;
            try {
                httpGet = new HttpGet(url);
                httpGet.setConfig(HttpClientUtils.getProxyRequestConfig());
            }
            catch (Exception e) {
                logger.error(String.format("url 错误: [%s]%s[%s]",
                        url, System.getProperty("line.separator"), e.getMessage()));
                continue;
            }

            // 创建HttpClient实例
            HttpClient httpclient = HttpClientUtils.getHttpClient();
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpclient.execute(httpGet);
                HttpEntity entity = httpResponse.getEntity();
                if (entity == null) {
                    throw new RuntimeException("请求失败.");
                }

                try (InputStream is = entity.getContent();
                       FileOutputStream os = new FileOutputStream(new File(filename))) {
                    IOUtils.copy(is, os);
                    os.flush();
                }

                httpGet.abort();

                ImageBean imageBean = new ImageBean(sid, url);
                AbstractRecord record = new ImageBeanRecord(imageBean);
                DBThreadManager.saveRecord(record);
            }
            catch (IOException e) {
                logger.error(String.format("下载图片 [%s] 失败.%s[%s]",
                        url, System.getProperty("line.separator"), e.getMessage()));
                e.printStackTrace();
                continue;
            }

            logger.info(String.format("下载图片 [%s] 成功.[当前/全部: %d/%d]", url, i+1, size));
        }

    }

    public String getName() {
        return name;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
