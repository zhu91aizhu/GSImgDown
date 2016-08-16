package com.jpycrgo.gsimgdown.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.BooleanUtils;
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
        Preconditions.checkArgument(StringUtils.isNotBlank(url), "url 为空");

        // 创建HttpClient实例
        HttpClient httpclient = HttpClientUtils.getHttpClient();
        // 创建Get方法实例
        HttpGet httpGet = new HttpGet(url);

        // 是否开启代理
        boolean proxyEnable = BooleanUtils.toBoolean(PropertiesUtils.getProperty("proxy-enable", "false"));
        if (proxyEnable) {
            httpGet.setConfig(HttpClientUtils.getProxyRequestConfig());
        }

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
