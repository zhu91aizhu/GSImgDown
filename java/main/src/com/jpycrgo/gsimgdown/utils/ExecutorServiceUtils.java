package com.jpycrgo.gsimgdown.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author mengzx
 * @date 2016/7/21
 * @since 1.0.0
 */
public class ExecutorServiceUtils {

    public static void awaitTermination(ExecutorService service) throws InterruptedException {
        while(!service.isTerminated()) {
            boolean termination = service.awaitTermination(60, TimeUnit.SECONDS);
            if (termination) {
                break;
            }
        }
    }

}
