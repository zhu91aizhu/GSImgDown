package com.jpycrgo.gsimgdown;

import com.jpycrgo.gsimgdown.cmdline.Cmdline;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author mengzx
 * @date 2016/4/27
 * @since 1.0.0
 */
public class Main {

    static {
        File file = new File(System.getProperty("user.dir"), "conf/log4j.xml");
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            final ConfigurationSource source = new ConfigurationSource(in);
            Configurator.initialize(null, source);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            Cmdline cmdline = new Cmdline();
            cmdline.loop();
        }
        catch (Exception e) {

        }

    }

}