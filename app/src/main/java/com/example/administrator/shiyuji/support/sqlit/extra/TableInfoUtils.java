package com.example.administrator.shiyuji.support.sqlit.extra;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.shiyuji.util.annotation.TableName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator on 2019/7/8.
 */

public class TableInfoUtils {

    public static final String TAG = "SqliteUtility";
    private static final HashMap<String, TableInfo> tableInfoMap = new HashMap();

    public TableInfoUtils() {
    }

    /**
     * 判断这个类有没有类名注解
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz) {
        TableName table = (TableName)clazz.getAnnotation(TableName.class);
        return table != null && table.table().trim().length() != 0?table.table():clazz.getName().replace('.', '_');
    }

    /**
     * 判断表存不存在
     * @param dbName
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> TableInfo exist(String dbName, Class<T> clazz) {
        return (TableInfo)tableInfoMap.get(dbName + "-" + getTableName(clazz));
    }

    /**
     * 如果表存在则增加新字段，不存在则创建新表
     * @param dbName
     * @param db
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> TableInfo newTable(String dbName, SQLiteDatabase db, Class<T> clazz) {
        Cursor cursor = null;
        TableInfo tableInfo = new TableInfo(clazz);
        tableInfoMap.put(dbName + "-" + getTableName(clazz), tableInfo);

        try {
            String e = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type =\'table\' AND name =\'" + tableInfo.getTableName() + "\' ";
            cursor = db.rawQuery(e, (String[])null);
            if(cursor != null && cursor.moveToNext()) {
                int createSql = cursor.getInt(0);
                if(createSql > 0) {
                    cursor.close();
//                    Logger.d("SqliteUtility", "表 %s 已存在", new Object[]{tableInfo.getTableName()});
                    cursor = db.rawQuery("PRAGMA table_info(" + tableInfo.getTableName() + ")", (String[])null);
                    ArrayList tableColumns = new ArrayList();
                    if(cursor != null && cursor.moveToNext()) {
                        do {
                            tableColumns.add(cursor.getString(cursor.getColumnIndex("name")));
                        } while(cursor.moveToNext());
                    }

                    cursor.close();
                    ArrayList properList = new ArrayList();
                    Iterator newFieldList = tableInfo.getColumns().iterator();

                    while(newFieldList.hasNext()) {
                        TableColumn column = (TableColumn)newFieldList.next();
                        properList.add(column.getColumn());
                    }

                    ArrayList newFieldList1 = new ArrayList();
                    Iterator column1 = properList.iterator();

                    String newField;
                    while(column1.hasNext()) {
                        newField = (String)column1.next();
                        if(!tableInfo.getPrimaryKey().equals(newField)) {
                            boolean isNew = true;
                            Iterator var13 = tableColumns.iterator();

                            while(var13.hasNext()) {
                                String tableColumn = (String)var13.next();
                                if(tableColumn.equals(newField)) {
                                    isNew = false;
                                    break;
                                }
                            }

                            if(isNew) {
                                newFieldList1.add(newField);
                            }
                        }
                    }

                    column1 = newFieldList1.iterator();

                    while(column1.hasNext()) {
                        newField = (String)column1.next();
                        db.execSQL(String.format("ALTER TABLE %s ADD %s TEXT", new Object[]{tableInfo.getTableName(), newField}));
//                        Logger.d("SqliteUtility", "表 %s 新增字段 %s", new Object[]{tableInfo.getTableName(), newField});
                    }

                    TableInfo column2 = tableInfo;
                    return column2;
                }
            }

            String createSql1 = SqlUtils.getTableSql(tableInfo);
            db.execSQL(createSql1);
//            Logger.d("SqliteUtility", "创建一张新表 %s", new Object[]{tableInfo.getTableName()});
        } catch (Exception var18) {
            var18.printStackTrace();
//            Logger.d("SqliteUtility", var18.getMessage() + "");
        } finally {
            if(cursor != null) {
                cursor.close();
            }

            cursor = null;
        }

        return tableInfo;
    }
}
