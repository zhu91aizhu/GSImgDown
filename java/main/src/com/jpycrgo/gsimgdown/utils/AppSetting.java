package com.jpycrgo.gsimgdown.utils;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * @author mengzx
 * @date 2016/8/3
 * @since 1.0.0
 */
public class AppSetting {

    private static final XMLConfiguration CONFIG;

    private static final Logger LOGGER = LogManager.getLogger(AppSetting.class);

    static {
        File config = new File(System.getProperty("user.dir"), "conf/application-setting.xml");
        BasicConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                .configure(new Parameters().xml().setFile(config));
        try {
            CONFIG = builder.getConfiguration();
        }
        catch (ConfigurationException e) {
            LOGGER.error("解析配置文件错误：" + e.getMessage());
            throw new RuntimeException("解析配置文件错误", e);
        }

        CONFIG.setExpressionEngine(new XPathExpressionEngine());
    }

    /**
     * 获取数据库URL
     */
    public static String getDatabaseURL() {
        return CONFIG.getString("database");
    }

    /**
     * 获取保存路径
     */
    public static String getSavePath() {
        return CONFIG.getString("save-path");
    }

    /**
     * 是否开启分页设置
     */
    public static boolean isEnablePagination() {
        return CONFIG.getBoolean("pagination/enable", false);
    }

    /**
     * 获取分页开始索引
     * 开始索引为 0
     * 如果没有开启分页设置，则返回 null
     */
    public static Integer getBeginIndex() {
        if (!isEnablePagination()) {
            return null;
        }

        return CONFIG.getInteger("pagination/begin", 0);
    }

    /**
     * 获取分页结束索引
     * 开始索引为 0
     * 如果没有开启分页设置，则返回 null
     * 如果没有配置结束索引，则返回 null
     */
    public static Integer getEndIndex() {
        if (!isEnablePagination()) {
            return null;
        }

        return CONFIG.getInteger("pagination/end", null);
    }

    /**
     * 是否开启代理设置
     */
    public static boolean isEnableProxy() {
        return CONFIG.getBoolean("proxy-setting/enable", false);
    }

    /**
     * 获取检查器类型
     */
    public static String getCheckor() {
        return CONFIG.getString("check-setting", "NO");
    }

    /**
     * 获取下载线程数量
     */
    public static int getDownloadThreadCount() {
        return CONFIG.getInt("download-thread-count", Runtime.getRuntime().availableProcessors() * 2);
    }

}
