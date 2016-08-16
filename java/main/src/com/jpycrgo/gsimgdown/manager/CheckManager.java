package com.jpycrgo.gsimgdown.manager;

import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

/**
 * @author mengzx
 * @date 2016/6/16
 * @since 1.0.2
 */
public class CheckManager {

    private static CheckType CHECKOR_TYPE = CheckType.NO;

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
            CHECKOR_TYPE = CheckType.IMAGE_THEME;
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
        return checkor != null && checkor.check();
    }

    /**
     * 生成检验器
     * @param param 检验参数
     * @param checkType 检验类型
     * @return 检验器
     */
    private static Checkable generateCheckor(String param, CheckManager.CheckType checkType) {
        if (checkType == null || !CHECKOR_TYPE.equals(checkType)) {
            return null;
        }

        Checkable checkor = checkType.generateCheckor(param);
        return checkor;
    }

    /**
     * 检验类型
     */
    public enum CheckType {
        NO(param -> null),
        FILE(param -> new FileCheckor(param)),
        IMAGE(param -> new ImageCheckor(param)),
        IMAGE_THEME(param -> new ImageThemeCheckor(param));

        private Function<String, ? extends Checkable> func;

        CheckType(Function<String, ? extends Checkable> func) {
            this.func = func;
        }

        public Checkable generateCheckor(String param) {
            return func.apply(param);
        }
    }

}
