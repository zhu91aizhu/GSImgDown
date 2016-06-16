package com.jpycrgo.gsimgdown.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

/**
 * @author mengzx
 * @date 2016/5/5
 * @since 1.0.0
 */
public class HttpClientUtils {

    private static final HttpClient HTTP_CLIENT;

    private static final HttpHost PROXY_HTTPHOST;

    private static final RequestConfig REQUEST_CONFIG;

    static {
        HTTP_CLIENT = HttpClients.createDefault();

        PROXY_HTTPHOST = new HttpHost(ConstantsUtils.PROXY_CONFIG.getHost(), ConstantsUtils.PROXY_CONFIG.getPort(),
                ConstantsUtils.PROXY_CONFIG.getProxyType());

        REQUEST_CONFIG = RequestConfig.custom().setProxy(PROXY_HTTPHOST).build();
    }

    public static HttpClient getHttpClient() {
        return HTTP_CLIENT;
    }

    public static HttpHost getProxyHttphost() {
        return PROXY_HTTPHOST;
    }

    public static RequestConfig getProxyRequestConfig() {
        return REQUEST_CONFIG;
    }

}
