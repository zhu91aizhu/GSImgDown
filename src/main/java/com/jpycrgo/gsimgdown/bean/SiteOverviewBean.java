package com.jpycrgo.gsimgdown.bean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * ’˚’æÕº∆¨∏≈¿¿
 * @author mengzx
 * @date 2016/4/28
 * @since 1.0.0
 */
public class SiteOverviewBean {

    public static final String DATA_COUNT_CODE;
    public static final String PAGE_SIZE_CODE;

    private int dataTotal;
    private int dataPageSize;

    static {
        DATA_COUNT_CODE = "data-count";
        PAGE_SIZE_CODE = "data-pagesize";
    }

    public SiteOverviewBean(int dataTotal, int dataPageSize) {
        this.dataTotal = dataTotal;
        this.dataPageSize = dataPageSize;
    }

    public static SiteOverviewBean parseDataOverview(String dataTotal, String dataPageSize) {
        if (StringUtils.isBlank(dataTotal) || StringUtils.isBlank(dataPageSize)) {
            throw new IllegalArgumentException("[argument(dataTotal) index 0] or [argument(dataPageSize) index 1] is blank");
        }

        int total = NumberUtils.toInt(dataTotal);
        int pageSize = NumberUtils.toInt(dataPageSize);
        return new SiteOverviewBean(total, pageSize);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public int getDataTotal() {
        return dataTotal;
    }

    public int getDataPageSize() {
        return dataPageSize;
    }
}
