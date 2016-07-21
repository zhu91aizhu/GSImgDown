package com.jpycrgo.gsimgdown.baseapi.db;

import com.jpycrgo.gsimgdown.baseapi.db.bean.AbstractRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author mengzx
 * @date 2016/5/13
 * @since 1.1
 */
public class SaveRecordDBTask implements Runnable {

    private AbstractRecord record;

    private static final Logger LOGGER = LogManager.getLogger(SaveRecordDBTask.class);

    public SaveRecordDBTask(AbstractRecord record) {
        this.record = record;
    }

    @Override
    public void run() {
        boolean success = DBHelper.insertRecord(DBExecutorServiceManager.getConnection(), record);
        if (success) {
            LOGGER.info(String.format("向数据库中插入 [%s] 成功", record.getRecordName()));
        }
        else {
            LOGGER.info(String.format("向数据库中插入 [%s] 失败", record.getRecordName()));
        }
    }

}
