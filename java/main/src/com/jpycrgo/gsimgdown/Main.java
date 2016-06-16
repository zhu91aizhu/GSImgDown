package com.jpycrgo.gsimgdown;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import com.jpycrgo.gsimgdown.bean.JsonParamBean;
import com.jpycrgo.gsimgdown.utils.DocumentUtils;
import com.jpycrgo.gsimgdown.bean.SiteOverviewBean;
import com.jpycrgo.gsimgdown.utils.HttpClientUtils;
import com.jpycrgo.gsimgdown.utils.PropertiesUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

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

    private static final String BASE_URL = "http://db2.gamersky.com/LabelJsonpAjax.aspx?jsondata=";

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        Document document = DocumentUtils.getUrlDocument("http://www.gamersky.com/ent/wp/");
        Elements elements = document.body().select("a[data-count]");
        Element element = elements.get(0);
        SiteOverviewBean dataOverview = getDataOverview(element);
        int pageTotal = getPageTotal(dataOverview.getDataTotal(), dataOverview.getDataPageSize());

        int beginPageIndex = PropertiesUtils.getIntProperty("begin-page-index");
        int endPageIndex = PropertiesUtils.getIntProperty("end-page-index", pageTotal);
        if (beginPageIndex > endPageIndex) {
            logger.error("开始页数或结束页数参数配置有误.");
            System.exit(1);
        }

        logger.info("下载开始页数：" + beginPageIndex);
        logger.info("下载结束页数：" + endPageIndex);

        JsonParamBean jsonParamBean = new JsonParamBean();
        BlockingQueue<ImageThemeBean> imageThemeSetBeans = new LinkedBlockingQueue<ImageThemeBean>();
        for (int i = beginPageIndex; i < endPageIndex; i++) {
            int pageIndex = i + 1;
            jsonParamBean.setPageIndex(pageIndex);

            Document imageThemeSet = getImageThemeSet(jsonParamBean);
            Elements themeSetElements = imageThemeSet.body().select("li");
            for (Element themeSetElement : themeSetElements) {
                Element tit = themeSetElement.select("div[class='tit']>a").get(0);
                String url = tit.attr("href");
                String title = tit.attr("title");

                Element txt = themeSetElement.select("div[class='txt']").get(0);
                String description = tit.text();

                Element pls = themeSetElement.select("div[class='pls cy_comment']").get(0);
                String sid = pls.attr("data-sid");

                ImageThemeBean imageThemeSetBean = new ImageThemeBean(title, description, url, sid);
                logger.info(String.format("add %s [%s]", title, url));
                imageThemeSetBeans.add(imageThemeSetBean);
            }
        }

        ImageDownloader.leftImageThemeCount = new AtomicInteger(imageThemeSetBeans.size());

        ImageDownloadTask thread = new ImageDownloadTask(imageThemeSetBeans, PropertiesUtils.getProperty("save_img_path"));
        Thread[] threads = new Thread[4];
        for (int i=0; i<4; i++) {
            threads[i] = new Thread(thread);
            threads[i].setName("IMAGE-DOWNLOADER-" + threads[i].getName());
            threads[i].start();
        }

        for(int i=0; i<4; i++) {
            threads[i].join();
        }

        logger.error("任务执行完成，程序正在退出.");
        System.exit(0);
    }

    private static Document getImageThemeSet(JsonParamBean jsonParamBean) throws Exception {
        // 创建Get方法实例
        HttpGet httpGet = new HttpGet(BASE_URL + jsonParamBean.parseParams());
        RequestConfig config = HttpClientUtils.getProxyRequestConfig();
        httpGet.setConfig(config);

        // 创建HttpClient实例
        HttpClient httpclient = HttpClientUtils.getHttpClient();
        HttpResponse httpResponse = httpclient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            throw new RuntimeException("request is failure.");
        }

        InputStream instream = entity.getContent();
        String content = CharStreams.toString(new InputStreamReader(instream, "UTF-8"));
        instream.close();
        httpGet.abort();

        if (!content.startsWith("(")) {
            throw new RuntimeException("return data format error.");
        }
        content = content.substring(1, content.length() - 2);

        ObjectMapper objectMapper = new ObjectMapper();
        Map resultMap = objectMapper.readValue(content, Map.class);
        String body = Objects.toString(resultMap.get("body"));
        return Jsoup.parse(body);
    }

    private static int getPageTotal(int dataTotal, int pageSize) {
        if (dataTotal % pageSize == 0) {
            return dataTotal / pageSize;
        }

        return dataTotal / pageSize + 1;
    }

    private static SiteOverviewBean getDataOverview(Element element) {
        assert element == null : "argument[element] index 0 is null";

        return SiteOverviewBean.parseDataOverview(element.attr(SiteOverviewBean.DATA_COUNT_CODE),
                element.attr(SiteOverviewBean.PAGE_SIZE_CODE));
    }

}