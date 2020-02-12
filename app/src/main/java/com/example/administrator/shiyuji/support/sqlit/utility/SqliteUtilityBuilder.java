package com.example.administrator.shiyuji.support.sqlit.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2019/7/8.
 */

public class SqliteUtilityBuilder {
    private int version = 1;
    private String dbName = "com_m_default_db";
    private boolean sdcardDb = false;

    public SqliteUtilityBuilder() {
    }

    public SqliteUtilityBuilder configVersion(int version) {
        this.version = version;
        return this;
    }
    public SqliteUtilityBuilder configDBName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public SqliteUtility build(Context context) {
        SQLiteDatabase writableDB = null;
        SQLiteDatabase readableDB = null;
        if(this.sdcardDb) {
//            writableDB = openSdcardDb(this.path, this.dbName, this.version);
            readableDB = writableDB;
//            Logger.d("SqliteUtility", String.format(String.format("打开app库 %s, version = %d", new Object[]{this.dbName, Integer.valueOf(writableDB.getVersion())}), new Object[0]));
        } else {
            SqliteUtilityBuilder.SqliteDbHelper dbHelper = new SqliteUtilityBuilder.SqliteDbHelper(context, this.dbName, this.version);
            writableDB = dbHelper.getWritableDatabase();
            readableDB = dbHelper.getReadableDatabase();
//            Logger.d("SqliteUtility", String.format(String.format("打开sdcard库 %s, version = %d", new Object[]{this.dbName, Integer.valueOf(readableDB.getVersion())}), new Object[0]));
        }

        return new SqliteUtility(this.dbName, writableDB, readableDB);
    }

    static void dropDb(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type =\'table\' AND name != \'sqlite_sequence\'", (String[])null);
        if(cursor != null) {
            while(cursor.moveToNext()) {
                db.execSQL("DROP TABLE " + cursor.getString(0));
//                Logger.d("SqliteUtility", "删除表 = " + cursor.getString(0));
            }
        }

        if(cursor != null) {
            cursor.close();
            cursor = null;
        }

    }

    static class SqliteDbHelper extends SQLiteOpenHelper {
        SqliteDbHelper(Context context, String dbName, int dbVersion) {
            super(context, dbName, (SQLiteDatabase.CursorFactory)null, dbVersion);
        }

        public void onCreate(SQLiteDatabase db) {
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            SqliteUtilityBuilder.dropDb(db);
            this.onCreate(db);
        }
    }
}
