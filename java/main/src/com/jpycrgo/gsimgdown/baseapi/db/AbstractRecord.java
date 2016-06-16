package com.jpycrgo.gsimgdown.baseapi.db;

/**
 * @author mengzx
 * @date 2016/6/16
 * @since 1.0.1
 */
public abstract class AbstractRecord {

    protected String recordName;

    public abstract String generateSQL();

    public abstract Object[] getParams();

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String name) {
        recordName = name;
    }

}
