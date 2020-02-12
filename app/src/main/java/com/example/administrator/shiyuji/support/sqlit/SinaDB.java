package com.example.administrator.shiyuji.support.sqlit;

import android.util.Log;

import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.support.sqlit.utility.SqliteUtility;
import com.example.administrator.shiyuji.support.sqlit.utility.SqliteUtilityBuilder;

/**
 * 数据库连接类
 * Created by Administrator on 2019/7/8.
 */

public class SinaDB {

    static final String DB_NAME = "sinadb";

    static final int DB_VERSION = 2;

    /**
     * 初始化数据库，在程序刚开始运行时就执行
     */
    public static void setInitDB() {
        try {
            Log.w("LScreenDB", "初始化 db versionCode = " + DB_VERSION);

            new SqliteUtilityBuilder().configVersion(DB_VERSION).configDBName(DB_NAME).build(GlobalContext.getInstance());
            new SqliteUtilityBuilder().configVersion(DB_VERSION).configDBName("sina_timeline_db").build(GlobalContext.getInstance());
        } catch (Throwable e) {
//            Logger.printExc(SinaDB.class, e);
        }
    }

    public static SqliteUtility getDB() {
        return SqliteUtility.getInstance(DB_NAME);
    }

    public static SqliteUtility getTimelineDB() {
        return SqliteUtility.getInstance("sina_timeline_db");
    }

}

