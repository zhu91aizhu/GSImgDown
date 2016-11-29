package com.jpycrgo.gsimgdown.test;

import com.jpycrgo.gsimgdown.utils.ExecutorServiceUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mengzx on 2016/8/16.
 */
public class ExecutorServiceUtilsTest {

    @Test
    public void testAwaitTermination() throws Exception {
        ExecutorServiceUtils.awaitTermination(null);
    }
}