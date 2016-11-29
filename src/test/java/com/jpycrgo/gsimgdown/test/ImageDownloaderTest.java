package com.jpycrgo.gsimgdown.test;

import com.jpycrgo.gsimgdown.baseapi.net.ImageDownloader;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by mengzx on 2016/8/16.
 */
public class ImageDownloaderTest {

    @Test
    public void testNewDownloader() throws Exception {
        ImageDownloader downloader = new ImageDownloader(null, null, "");
        Assert.assertNotNull(downloader);
    }

}