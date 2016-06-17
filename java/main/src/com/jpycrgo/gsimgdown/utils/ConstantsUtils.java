package com.jpycrgo.gsimgdown.utils;

import com.jpycrgo.gsimgdown.ProxyConfig;

/**
 * @author mengzx
 * @date 2016/4/29
 * @since 1.0.0
 */
public class ConstantsUtils {

    private static final String PROXY_HOST = "isadz.hold.founder.com";
    private static final int PROXY_PORT = 80;
    private static final String PROXY_TYPE = "http";
    public static final ProxyConfig PROXY_CONFIG;

    public static final int DEFAULT_DOWNLOADTHREAD_COUNT = 4;

    private static final String BASE_URL = "http://db2.gamersky.com/LabelJsonpAjax.aspx?jsondata=";

    static {
        PROXY_CONFIG = new ProxyConfig.Builder(PROXY_HOST).port(PROXY_PORT).proxyType(PROXY_TYPE).build();
    }

}
