package com.jpycrgo.gsimgdown.manager;

import java.io.File;

/**
 * @author mengzx
 * @date 2016/6/16
 * @since 1.0.2
 */
public class FileCheckor implements Checkable {

    private String filepath;

    public FileCheckor(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public boolean check() {
        File checkFile = new File(filepath);
        if (checkFile.exists()) {
            return true;
        }

        return false;
    }

}
