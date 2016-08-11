package com.jpycrgo.gsimgdown.manager;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mengzx
 * @date 2016/6/16
 * @since 1.0.2
 */
public class CheckManager {

    private static CheckType CHECKOR_TYPE;

    /**
     * 设置检验类型
     * @param checktype 检验类型
     */
    public static void setCheckType(String checktype) {
        if (StringUtils.equals(checktype, "no")) {
            CHECKOR_TYPE = CheckType.NO;
        }

        if (StringUtils.equals(checktype, "file")) {
            CHECKOR_TYPE = CheckType.FILE;
        }

        if (StringUtils.equals(checktype, "image")) {
            CHECKOR_TYPE = CheckType.IMAGE;
        }

        if (StringUtils.equals(checktype, "imagetheme")) {
            CHECKOR_TYPE = CheckType.IMAGETHEME;
        }
    }

    /**
     * 检验是否存在记录
     * @param param 检验器参数
     * @param checkType 检查器类型
     * @return 存在放回 true, 否则返回 false
     */
    public static boolean check(String param, CheckManager.CheckType checkType) {
        Checkable checkor = generateCheckor(param, checkType);
        if (checkor == null) {
            return false;
        }

        return checkor.check();
    }

    /**
     * 生成检验器
     * @param param 检验参数
     * @param checkType 检验类型
     * @return 检验器
     */
    private static Checkable generateCheckor(String param, CheckManager.CheckType checkType) {
        if (!checkType.equals(CHECKOR_TYPE)) {
            return null;
        }

        Checkable checkor = null;
        switch (checkType) {
            case NO:
                checkor = null;
                break;
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
    public enum CheckType {
        NO, FILE, IMAGE, IMAGETHEME
    }

}
