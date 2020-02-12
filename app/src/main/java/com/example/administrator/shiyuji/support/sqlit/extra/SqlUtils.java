package com.example.administrator.shiyuji.support.sqlit.extra;

import android.text.TextUtils;

import com.example.administrator.shiyuji.sdk.cache.Extra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2019/7/8.
 */

public class SqlUtils {

    public SqlUtils() {
    }


    /**
     * 得到插入sql语句
     * @param insertInto
     * @param tableInfo
     * @return
     */
    public static String createSqlInsert(String insertInto, TableInfo tableInfo) {
        ArrayList columns = new ArrayList();
        if(!(tableInfo.getPrimaryKey() instanceof AutoIncrementTableColumn)) {
            columns.add(tableInfo.getPrimaryKey());
        }

        columns.addAll(tableInfo.getColumns());
        columns.add(FieldUtils.getOwnerColumn());
        columns.add(FieldUtils.getKeyColumn());
        columns.add(FieldUtils.getCreateAtColumn());
        StringBuilder builder = new StringBuilder(insertInto);
        builder.append(tableInfo.getTableName()).append(" (");
        appendColumns(builder, columns);
        builder.append(") VALUES (");
        appendPlaceholders(builder, columns.size());
        builder.append(')');
        return builder.toString();
    }

    /**
     * 将列表中的数据按格式形成字符串
     * @param builder
     * @param columns
     * @return
     */
    public static StringBuilder appendColumns(StringBuilder builder, List<TableColumn> columns) {
        int length = columns.size();

        for(int i = 0; i < length; ++i) {
            builder.append('\'').append(((TableColumn)columns.get(i)).getColumn()).append('\'');
            if(i < length - 1) {
                builder.append(',');
            }
        }

        return builder;
    }
    public static StringBuilder appendPlaceholders(StringBuilder builder, int count) {
        for(int i = 0; i < count; ++i) {
            if(i < count - 1) {
                builder.append("?,");
            } else {
                builder.append('?');
            }
        }

        return builder;
    }
    /**
     * 得到创建一张表的sql语句
     * @param table
     * @return
     */
    public static String getTableSql(TableInfo table) {
        TableColumn primaryKey = table.getPrimaryKey();
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("CREATE TABLE IF NOT EXISTS ");
        strSQL.append(table.getTableName());
        strSQL.append(" ( ");
        if(primaryKey instanceof AutoIncrementTableColumn) {
            strSQL.append(" ").append(primaryKey.getColumn()).append(" ").append(" INTEGER PRIMARY KEY AUTOINCREMENT ,");
        } else {
            strSQL.append(" ").append(primaryKey.getColumn()).append(" ").append(primaryKey.getColumnType()).append(" NOT NULL ,");
        }

        Iterator tableStr = table.getColumns().iterator();

        while(tableStr.hasNext()) {
            TableColumn column = (TableColumn)tableStr.next();
            strSQL.append(" ").append(column.getColumn());
            strSQL.append(" ").append(column.getColumnType());
            strSQL.append(" ,");
        }

        strSQL.append(" ").append("com_m_common_owner").append(" text NOT NULL, ");
        strSQL.append(" ").append("com_m_common_key").append(" text NOT NULL, ");
        strSQL.append(" ").append("com_m_common_createat").append(" INTEGER NOT NULL ");
        if(!(primaryKey instanceof AutoIncrementTableColumn)) {
            strSQL.append(", PRIMARY KEY ( ").append(primaryKey.getColumn()).append(" , ").append("com_m_common_key").append(" , ").append("com_m_common_owner").append(" )");
        }

        strSQL.append(" )");
        String tableStr1 = strSQL.toString();
//        Logger.d("SqliteUtility", "create table = " + tableStr1);
        return tableStr1;
    }

    /**
     * 将extra中的信息转化为String
     * @param extra     缓存时用到的类
     * @return
     */
    public static String appendExtraWhereClauseSql(Extra extra) {
        StringBuffer sb = new StringBuffer();
        if(extra != null && (!TextUtils.isEmpty(extra.getKey()) || !TextUtils.isEmpty(extra.getOwner()))) {
            if(!TextUtils.isEmpty(extra.getKey()) && !TextUtils.isEmpty(extra.getOwner())) {
                sb.append(" ").append("com_m_common_owner").append(" = \'").append(extra.getOwner()).append("\' ").append(" and ").append("com_m_common_key").append(" = \'").append(extra.getKey()).append("\' ");
            } else if(!TextUtils.isEmpty(extra.getKey())) {
                sb.append("com_m_common_key").append(" = \'").append(extra.getKey()).append("\' ");
            } else if(!TextUtils.isEmpty(extra.getOwner())) {
                sb.append(" ").append("com_m_common_owner").append(" = \'").append(extra.getOwner()).append("\' ");
            }
        } else {
            sb.append("");
        }

        return sb.toString();
    }
    public static String appendExtraWhereClause(Extra extra) {
        StringBuffer sb = new StringBuffer();
        if(extra != null && (!TextUtils.isEmpty(extra.getKey()) || !TextUtils.isEmpty(extra.getOwner()))) {
            if(!TextUtils.isEmpty(extra.getKey()) && !TextUtils.isEmpty(extra.getOwner())) {
                sb.append(" ").append("com_m_common_key").append(" = ? ").append(" and ").append("com_m_common_owner").append(" = ? ");
            } else if(!TextUtils.isEmpty(extra.getKey())) {
                sb.append("com_m_common_key").append(" = ? ");
            } else if(!TextUtils.isEmpty(extra.getOwner())) {
                sb.append(" ").append("com_m_common_owner").append(" = ? ");
            }
        } else {
            sb.append("");
        }

        return sb.toString();
    }
    public static String[] appendExtraWhereArgs(Extra extra) {
        ArrayList argList = new ArrayList();
        if(extra != null) {
            if(!TextUtils.isEmpty(extra.getKey())) {
                argList.add(extra.getKey());
            }

            if(!TextUtils.isEmpty(extra.getOwner())) {
                argList.add(extra.getOwner());
            }
        }

        return (String[])argList.toArray(new String[0]);
    }
}
