package com.jpycrgo.gsimgdown.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author mengzx
 * @date 2016/5/5
 * @since 1.0.0
 */
public class DocumentUtils {


    public static Document getUrlDocument(String url) throws IOException {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url is blank");
        }

        // 创建HttpClient实例
        HttpClient httpclient = HttpClientUtils.getHttpClient();
        // 创建Get方法实例
        HttpGet httpGet = new HttpGet(url);
        RequestConfig config = HttpClientUtils.getProxyRequestConfig();
        httpGet.setConfig(config);

        Document document = null;
        HttpResponse httpResponse = httpclient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            InputStream instreams = entity.getContent();
            document = Jsoup.parse(instreams, "UTF-8", url);
            instreams.close();
            httpGet.abort();
        }

        return document;
    }
}
