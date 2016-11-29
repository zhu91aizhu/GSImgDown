package com.jpycrgo.gsimgdown.test;

import com.jpycrgo.gsimgdown.manager.CheckManager;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mengzx on 2016/8/16.
 */
public class CheckManagerTest {

    @Test
    public void testCheck() throws Exception {
        CheckManager.setCheckType("file");
        CheckManager.check("", CheckManager.CheckType.FILE);
    }
}