package com.jpycrgo.gsimgdown.baseapi.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.jpycrgo.gsimgdown.bean.ImageThemeBean;
import com.jpycrgo.gsimgdown.bean.JsonParamBean;
import com.jpycrgo.gsimgdown.bean.SiteOverviewBean;
import com.jpycrgo.gsimgdown.utils.AppSetting;
import com.jpycrgo.gsimgdown.utils.DocumentUtils;
import com.jpycrgo.gsimgdown.utils.HttpClientUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author mengzx
 * @date 2016/7/19
 * @since 1.0.0
 */
public class ImageSiteAnalyzer {

    private static final String BASE_URL = "http://db2.gamersky.com/LabelJsonpAjax.aspx?jsondata=";

    private static final Logger LOGGER = LogManager.getLogger("applog");

    private int pageTotal;

    private int beginPageIndex = 0;

    private int endPageIndex;

    public ImageSiteAnalyzer(String site) throws IOException {
        Document document = DocumentUtils.getUrlDocument(site);
        Elements elements = document.body().select("a[data-count]");
        Element element = elements.get(0);
        SiteOverviewBean dataOverview = ImageSiteAnalyzer.getDataOverview(element);

        pageTotal = ImageSiteAnalyzer.getPageTotal(dataOverview.getDataTotal(), dataOverview.getDataPageSize());
        endPageIndex = pageTotal;
    }

    public List<ImageThemeBean> analysis() throws Exception {
        List<ImageThemeBean> imageThemeSetBeans = Lists.newArrayList();
        JsonParamBean jsonParamBean = new JsonParamBean();
        for (int i = beginPageIndex; i < endPageIndex; i++) {
            int pageIndex = i + 1;
            jsonParamBean.setPageIndex(pageIndex);

            Document imageThemeSet = getImageThemeSet(jsonParamBean);
            Elements themeSetElements = imageThemeSet.body().select("li");
            for (Element themeSetElement : themeSetElements) {
                Element tit = themeSetElement.select("div[class='tit']>a").get(0);
                String url = tit.attr("href");
                String title = tit.attr("title");

                String description = tit.text();

                Element pls = themeSetElement.select("div[class='pls cy_comment']").get(0);
                String sid = pls.attr("data-sid");

                ImageThemeBean imageThemeSetBean = new ImageThemeBean(title, description, url, sid);
                ImageSiteAnalyzer.LOGGER.info(String.format("添加图片主题 [%s - %s]", title, url));
                imageThemeSetBeans.add(imageThemeSetBean);
            }
        }

        return imageThemeSetBeans;
    }

    private static SiteOverviewBean getDataOverview(Element element) {
        assert element == null : "argument[element] index 0 is null";

        return SiteOverviewBean.parseDataOverview(element.attr(SiteOverviewBean.DATA_COUNT_CODE),
                element.attr(SiteOverviewBean.PAGE_SIZE_CODE));
    }

    private static int getPageTotal(int dataTotal, int pageSize) {
        return dataTotal % pageSize == 0 ? dataTotal / pageSize : dataTotal / pageSize + 1;
    }

    private static Document getImageThemeSet(JsonParamBean jsonParamBean) throws Exception {
        // 创建Get方法实例
        HttpGet httpGet = new HttpGet(BASE_URL + jsonParamBean.parseParams());
        // 是否开启代理
        boolean proxyEnable = AppSetting.isEnableProxy();
        if (proxyEnable) {
            httpGet.setConfig(HttpClientUtils.getProxyRequestConfig());
        }

        // 创建HttpClient实例
        HttpClient httpclient = HttpClientUtils.getHttpClient();
        HttpResponse httpResponse = httpclient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            throw new RuntimeException("request is failure.");
        }

        String content;
        try (InputStream instream = entity.getContent()) {
            content = CharStreams.toString(new InputStreamReader(instream, "UTF-8"));
        }
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

    public void setBeginPageIndex(int beginPageIndex) {
        this.beginPageIndex = beginPageIndex;
    }

    public void setEndPageIndex(int endPageIndex) {
        this.endPageIndex = endPageIndex;
    }

    public int getPageTotal() {
        return pageTotal;
    }
}
