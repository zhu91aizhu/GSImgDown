package com.jpycrgo.gsimgdown.utils;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.PropertiesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author mengzx
 * @date 2016/5/13
 * @since 1.0.0
 */
public class PropertiesUtils {

    private static final Properties properties = new Properties();

    private static final Logger logger = LogManager.getLogger(PropertiesUtils.class);

    static {
        try {
            InputStream resourceInputStream = new FileInputStream(new File("conf/app.properties"));
            properties.load(resourceInputStream);
        }
        catch (IOException e) {
            logger.error("载入 app 配置文件错误.");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key, StringUtils.EMPTY);
    }

    public static int getIntProperty(String key) {
        String orgValue = getProperty(key);
        return NumberUtils.toInt(orgValue);
    }

    public static int getIntProperty(String key, int defaultValue) {
        String orgValue = getProperty(key);
        return NumberUtils.toInt(orgValue, defaultValue);
    }

}
