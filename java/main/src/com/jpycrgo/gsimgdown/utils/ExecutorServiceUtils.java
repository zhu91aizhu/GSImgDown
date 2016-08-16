package com.jpycrgo.gsimgdown.utils;

import com.google.common.base.Preconditions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author mengzx
 * @date 2016/7/21
 * @since 1.0.0
 */
public class ExecutorServiceUtils {

    public static void awaitTermination(ExecutorService service) throws InterruptedException {
        Preconditions.checkNotNull(service, "executor service is null.");

        while(!service.isTerminated()) {
            boolean termination = service.awaitTermination(5, TimeUnit.SECONDS);
            if (termination) {
                break;
            }
        }
    }

}
