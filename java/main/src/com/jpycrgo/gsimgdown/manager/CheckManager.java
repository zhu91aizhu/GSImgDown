package com.jpycrgo.gsimgdown.manager;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mengzx
 * @date 2016/6/16
 * @since 1.0.2
 */
public class CheckManager {

    private static boolean checkFileFlag = false;

    private static boolean checkImageFlag = false;

    private static boolean checkImageThemeFlag = false;

    /**
     * 设置检验类型
     * @param checktype 检验类型
     */
    public static void setCheckType(String checktype) {
        if (StringUtils.equals(checktype, "file")) {
            CheckManager.checkFileFlag = true;
        }

        if (StringUtils.equals(checktype, "image")) {
            CheckManager.checkImageFlag = true;
        }

        if (StringUtils.equals(checktype, "imagetheme")) {
            CheckManager.checkImageThemeFlag = true;
        }
    }

    /**
     * 检验是否存在记录
     * @param checkor 检验器
     * @return 存在放回 true, 否则返回 false
     */
    public static boolean check(Checkable checkor) {
        if (checkFileFlag && checkor instanceof FileCheckor && checkor.check()) {
            return true;
        }

        if (checkImageFlag && checkor instanceof ImageCheckor && checkor.check()) {
            return true;
        }

        if (checkImageThemeFlag && checkor instanceof ImageThemeCheckor && checkor.check()) {
            return true;
        }

        return false;
    }

    /**
     * 生成检验器
     * @param param 检验参数
     * @param checkType 检验类型
     * @return 检验器
     */
    public static Checkable generateCheckor(String param, CheckManager.CheckType checkType) {
        Checkable checkor = null;

        switch (checkType) {
            case FILE:
                checkor = new FileCheckor(param);
                break;
            case IMAGE:
                checkor = new ImageCheckor(param);
                break;
            case IMAGETHEME:
                checkor = new ImageThemeCheckor(param);
                break;
        }

        return checkor;
    }

    /**
     * 检验类型
     */
    public static enum CheckType {
        FILE, IMAGE, IMAGETHEME
    }

}
