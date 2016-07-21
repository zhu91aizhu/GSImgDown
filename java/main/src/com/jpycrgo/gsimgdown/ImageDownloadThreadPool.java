package com.jpycrgo.gsimgdown;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author mengzx
 * @date 2016/7/21
 * @since 1.0.0
 */
public class ImageDownloadThreadPool {

    private ExecutorService service;

    public ImageDownloadThreadPool(ExecutorService service) {
        this.service = service;
    }

    public void awaitTermination() throws InterruptedException {
        while(!service.isTerminated()) {
            boolean termination = service.awaitTermination(60, TimeUnit.SECONDS);
            if (termination) {
                break;
            }
        }
    }

}
