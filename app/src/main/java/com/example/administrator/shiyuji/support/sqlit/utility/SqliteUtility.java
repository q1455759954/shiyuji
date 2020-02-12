package com.example.administrator.shiyuji.support.sqlit.utility;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.example.administrator.shiyuji.sdk.cache.Extra;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.support.sqlit.extra.AutoIncrementTableColumn;
import com.example.administrator.shiyuji.support.sqlit.extra.SqlUtils;
import com.example.administrator.shiyuji.support.sqlit.extra.TableColumn;
import com.example.administrator.shiyuji.support.sqlit.extra.TableInfo;
import com.example.administrator.shiyuji.support.sqlit.extra.TableInfoUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2019/7/8.
 */

public class SqliteUtility {

    public static final String TAG = "SqliteUtility";
    private static Hashtable<String, SqliteUtility> dbCache = new Hashtable();
    private String dbName;
    private SQLiteDatabase writableDB;
    private SQLiteDatabase readableDB;

    SqliteUtility(String dbName, SQLiteDatabase writableDB, SQLiteDatabase readableDB) {
        this.writableDB = writableDB;
        this.readableDB = readableDB;
        this.dbName = dbName;
        dbCache.put(dbName, this);
//        Logger.d("SqliteUtility", "将库 %s 放到缓存中", new Object[]{dbName});
    }

    public static SqliteUtility getInstance(String dbName) {
        return (SqliteUtility)dbCache.get(dbName);
    }

    /**
     * 插入实体类json形式
     * @param extra
     * @param entities
     * @param <T>
     */
    public <T> void insert(Extra extra, T... entities) {
        try {
            if(entities != null && entities.length > 0) {
                this.insert(extra, Arrays.asList(entities));
            } else {
//                Logger.d("SqliteUtility", "method[insert(Extra extra, T... entities)], entities is empty");
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public <T> void insert(Extra extra, List<T> entityList) {
        try {
            this.insert(extra, entityList, "INSERT OR IGNORE INTO ");
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    public <T> List<T> select(Extra extra, Class<T> clazz) {
        String selection = SqlUtils.appendExtraWhereClause(extra);
        String[] selectionArgs = SqlUtils.appendExtraWhereArgs(extra);
        return this.select(clazz, selection, selectionArgs, (String)null, (String)null, (String)null, (String)null);
    }

    public <T> List<T> select(Class<T> clazz, String selection, String[] selectionArgs) {
        return this.select(clazz, selection, selectionArgs, (String)null, (String)null, (String)null, (String)null);
    }


    public <T> List<T> select(Class<T> clazz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        TableInfo tableInfo = this.checkTable(clazz);
        ArrayList list = new ArrayList();//存最终结果

        ArrayList columnList = new ArrayList();
        columnList.add(tableInfo.getPrimaryKey().getColumn());
        Iterator start = tableInfo.getColumns().iterator();

        //将表里的每个字段都加入到集合里
        while(start.hasNext()) {
            TableColumn tableColumn = (TableColumn)start.next();
            columnList.add(tableColumn.getColumn());
        }

        Cursor cursor = this.getReadableDB().query(tableInfo.getTableName(), (String[])columnList.toArray(new String[0]), selection, selectionArgs, groupBy, having, orderBy, limit);

        try {
            if(cursor.moveToFirst()) {
                do {
                    try {
                        Object e = clazz.newInstance();
                        this.bindSelectValue(e, cursor, tableInfo.getPrimaryKey());//将查询到的数据绑定到对象里
                        Iterator var15 = tableInfo.getColumns().iterator();

                        while(var15.hasNext()) {
                            TableColumn column = (TableColumn)var15.next();
                            this.bindSelectValue(e, cursor, column);
                        }

                        list.add(e);
                    } catch (Exception var20) {
                        var20.printStackTrace();
                    }
                } while(cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return list;
    }
    /**
     * 插入
     * @param extra
     * @param entityList
     * @param insertInto
     * @param <T>
     */
    private <T> void insert(Extra extra, List<T> entityList, String insertInto) {
        if(entityList != null && entityList.size() != 0) {
            TableInfo tableInfo = this.checkTable(entityList.get(0).getClass());
            synchronized(tableInfo) {
                long start = System.currentTimeMillis();
                this.getWritableDB().beginTransaction();

                try {
                    //得到插入sql语句
                    String cursor = SqlUtils.createSqlInsert(insertInto, tableInfo);
                    SQLiteStatement newId = this.getWritableDB().compileStatement(cursor);
                    long bean = 0L;
                    long startTime = System.currentTimeMillis();
                    Iterator var14 = entityList.iterator();

                    while(true) {
                        if(!var14.hasNext()) {
                            this.getWritableDB().setTransactionSuccessful();
                            break;
                        }

                        Object entity = var14.next();
                        this.bindInsertValues(extra, newId, tableInfo, entity);
                        bean += System.currentTimeMillis() - startTime;
                        startTime = System.currentTimeMillis();
                        newId.execute();
                    }
                } finally {
                    this.getWritableDB().endTransaction();
                }

                //如果list中只有一条数据，将这条数据（指list里的实体类，不是数据库中的数据）的这个字段设置自增长
                if(entityList.size() == 1 && tableInfo.getPrimaryKey() instanceof AutoIncrementTableColumn) {
                    Cursor cursor1 = null;

                    try {
                        cursor1 = this.getWritableDB().rawQuery("select last_insert_rowid() from " + tableInfo.getTableName(), (String[])null);
                        if(cursor1.moveToFirst()) {
                            int newId1 = cursor1.getInt(0);
                            Object bean1 = entityList.get(0);

                            try {
                                tableInfo.getPrimaryKey().getField().setAccessible(true);
                                tableInfo.getPrimaryKey().getField().set(bean1, Integer.valueOf(newId1));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    } finally {
                        if(cursor1 != null) {
                            cursor1.close();
                        }

                    }
                }
                Log.d("缓存：","加入缓存成功！");
            }
        } else {
        }
    }

    /**
     * 将entity中的数据设置到tableinfo里面，并与insertStatement绑定，最后绑定extra中的数据
     * @param extra
     * @param insertStatement
     * @param tableInfo
     * @param entity
     * @param <T>
     */
    private <T> void bindInsertValues(Extra extra, SQLiteStatement insertStatement, TableInfo tableInfo, T entity) {
        int index = 1;
        if(!(tableInfo.getPrimaryKey() instanceof AutoIncrementTableColumn)) {
            this.bindValue(insertStatement, index++, tableInfo.getPrimaryKey(), entity);
        }

        for(int owner = 0; owner < tableInfo.getColumns().size(); ++owner) {
            TableColumn key = (TableColumn)tableInfo.getColumns().get(owner);
            this.bindValue(insertStatement, index++, key, entity);
        }

        String var10 = extra != null && !TextUtils.isEmpty(extra.getOwner())?extra.getOwner():"";
        insertStatement.bindString(index++, var10);
        String var11 = extra != null && !TextUtils.isEmpty(extra.getKey())?extra.getKey():"";
        insertStatement.bindString(index++, var11);
        long createAt = System.currentTimeMillis();
        insertStatement.bindLong(index, createAt);
    }

    /**
     * 绑定数据
     * @param insertStatement
     * @param index
     * @param column
     * @param entity
     * @param <T>
     */
    private <T> void bindValue(SQLiteStatement insertStatement, int index, TableColumn column, T entity) {
        try {
            column.getField().setAccessible(true);
            Object e = column.getField().get(entity);
            if(e == null) {
                insertStatement.bindNull(index);
                return;
            }

            if("object".equalsIgnoreCase(column.getDataType())) {
                insertStatement.bindString(index, JSON.toJSONString(e));
            } else if("INTEGER".equalsIgnoreCase(column.getColumnType())) {
                insertStatement.bindLong(index, Long.parseLong(e.toString()));
            } else if("REAL".equalsIgnoreCase(column.getColumnType())) {
                insertStatement.bindDouble(index, Double.parseDouble(e.toString()));
            } else if("BLOB".equalsIgnoreCase(column.getColumnType())) {
                insertStatement.bindBlob(index, (byte[])((byte[])e));
            } else if("TEXT".equalsIgnoreCase(column.getColumnType())) {
                insertStatement.bindString(index, e.toString());
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    private <T> void bindValue(ContentValues values, TableColumn column, T entity) {
        try {
            column.getField().setAccessible(true);
            Object e = column.getField().get(entity);
            if(e == null) {
                return;
            }

            if("object".equalsIgnoreCase(column.getDataType())) {
                values.put(column.getColumn(), JSON.toJSONString(e));
            } else if("INTEGER".equalsIgnoreCase(column.getColumnType())) {
                values.put(column.getColumn(), Long.valueOf(Long.parseLong(e.toString())));
            } else if("REAL".equalsIgnoreCase(column.getColumnType())) {
                values.put(column.getColumn(), Double.valueOf(Double.parseDouble(e.toString())));
            } else if("BLOB".equalsIgnoreCase(column.getColumnType())) {
                values.put(column.getColumn(), (byte[])((byte[])e));
            } else if("TEXT".equalsIgnoreCase(column.getColumnType())) {
                values.put(column.getColumn(), e.toString());
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    /**
     * 将查询到的数据column绑定到entity中
     * @param entity
     * @param cursor
     * @param column
     * @param <T>
     */
    private <T> void bindSelectValue(T entity, Cursor cursor, TableColumn column) {
        Field field = column.getField();
        field.setAccessible(true);

        try {
            if(!field.getType().getName().equals("int") && !field.getType().getName().equals("java.lang.Integer")) {
                if(!field.getType().getName().equals("long") && !field.getType().getName().equals("java.lang.Long")) {
                    if(!field.getType().getName().equals("float") && !field.getType().getName().equals("java.lang.Float")) {
                        if(!field.getType().getName().equals("double") && !field.getType().getName().equals("java.lang.Double")) {
                            if(!field.getType().getName().equals("boolean") && !field.getType().getName().equals("java.lang.Boolean")) {
                                if(!field.getType().getName().equals("char") && !field.getType().getName().equals("java.lang.Character")) {
                                    if(!field.getType().getName().equals("byte") && !field.getType().getName().equals("java.lang.Byte")) {
                                        if(!field.getType().getName().equals("short") && !field.getType().getName().equals("java.lang.Short")) {
                                            if(field.getType().getName().equals("java.lang.String")) {
                                                field.set(entity, cursor.getString(cursor.getColumnIndex(column.getColumn())));
                                            } else if(field.getType().getName().equals("[B")) {
                                                field.set(entity, cursor.getBlob(cursor.getColumnIndex(column.getColumn())));
                                            } else {
                                                String e = cursor.getString(cursor.getColumnIndex(column.getColumn()));
                                                field.set(entity, JSON.parseObject(e, field.getGenericType(), new Feature[0]));
                                            }
                                        } else {
                                            field.set(entity, Short.valueOf(cursor.getShort(cursor.getColumnIndex(column.getColumn()))));
                                        }
                                    } else {
                                        field.set(entity, Byte.valueOf((byte)cursor.getInt(cursor.getColumnIndex(column.getColumn()))));
                                    }
                                } else {
                                    field.set(entity, Character.valueOf(cursor.getString(cursor.getColumnIndex(column.getColumn())).toCharArray()[0]));
                                }
                            } else {
                                field.set(entity, Boolean.valueOf(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(column.getColumn())))));
                            }
                        } else {
                            field.set(entity, Double.valueOf(cursor.getDouble(cursor.getColumnIndex(column.getColumn()))));
                        }
                    } else {
                        field.set(entity, Float.valueOf(cursor.getFloat(cursor.getColumnIndex(column.getColumn()))));
                    }
                } else {
                    field.set(entity, Long.valueOf(cursor.getLong(cursor.getColumnIndex(column.getColumn()))));
                }
            } else {
                field.set(entity, Integer.valueOf(cursor.getInt(cursor.getColumnIndex(column.getColumn()))));
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }
    /**
     * 删除缓存信息
     * @param extra
     * @param clazz
     * @param <T>
     */
    public <T> void deleteAll(Extra extra, Class<T> clazz) {
        try {
            TableInfo e = this.checkTable(clazz);
            String where = SqlUtils.appendExtraWhereClauseSql(extra);
            if(!TextUtils.isEmpty(where)) {
                where = " where " + where;
            }

            String sql = "DELETE FROM \'" + e.getTableName() + "\' " + where;
//            Logger.d("SqliteUtility", "method[delete] table[%s], sql[%s]", new Object[]{e.getTableName(), sql});
            long start = System.currentTimeMillis();
            this.getWritableDB().execSQL(sql);
//            Logger.d("SqliteUtility", "表 %s 清空数据, 耗时 %s ms", new Object[]{e.getTableName(), String.valueOf(System.currentTimeMillis() - start)});
        } catch (Exception var8) {
            var8.printStackTrace();
        }

    }

    /**
     * 查看表是否存在，不存在则新建
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> TableInfo checkTable(Class<T> clazz) {
        TableInfo tableInfo = TableInfoUtils.exist(this.dbName, clazz);
        if(tableInfo == null) {
            tableInfo = TableInfoUtils.newTable(this.dbName, this.writableDB, clazz);
        }

        return tableInfo;
    }

    /**
     * 插入或者替换
     * 草稿插入，
     * @param extra
     * @param entities
     * @param <T>
     */
    public <T> void insertOrReplace(Extra extra, T... entities) {
        try {
            if(entities != null && entities.length > 0) {
                this.insert(extra, Arrays.asList(entities), "INSERT OR REPLACE INTO ");
            } else {
//                Logger.d("SqliteUtility", "method[insertOrReplace(Extra extra, T... entities)], entities is empty");
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public SQLiteDatabase getReadableDB() {
        return this.readableDB;
    }
    public SQLiteDatabase getWritableDB() {
        return this.writableDB;
    }

    /**
     * 通过id得到，点赞信息
     * @param extra
     * @param clazz
     * @param id
     * @param <T>
     * @return
     */
    public <T> T selectById(Extra extra, Class<T> clazz, Object id) {
        try {
            TableInfo e = this.checkTable(clazz);
            String selection = String.format(" %s = ? ", new Object[]{e.getPrimaryKey().getColumn()});
            String extraSelection = SqlUtils.appendExtraWhereClause(extra);
            if(!TextUtils.isEmpty(extraSelection)) {
                selection = String.format("%s and %s", new Object[]{selection, extraSelection});
            }

            ArrayList selectionArgList = new ArrayList();
            selectionArgList.add(String.valueOf(id));
            String[] extraSelectionArgs = SqlUtils.appendExtraWhereArgs(extra);
            if(extraSelectionArgs != null && extraSelectionArgs.length > 0) {
                selectionArgList.addAll(Arrays.asList(extraSelectionArgs));
            }

            String[] selectionArgs = (String[])selectionArgList.toArray(new String[0]);
            List list = this.select(clazz, selection, selectionArgs, (String)null, (String)null, (String)null, (String)null);
            if(list.size() > 0) {
                return (T) list.get(0);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return null;
    }
}
