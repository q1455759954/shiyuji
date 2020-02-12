package com.example.administrator.shiyuji.support.sqlit.extra;

/**
 * Created by Administrator on 2019/7/8.
 */

public class FieldUtils {
    public static final String OWNER = "com_m_common_owner";
    public static final String KEY = "com_m_common_key";
    public static final String CREATEAT = "com_m_common_createat";

    public FieldUtils() {
    }

    public static TableColumn getOwnerColumn() {
        TableColumn column = new TableColumn();
        column.setColumn("com_m_common_owner");
        return column;
    }

    public static TableColumn getKeyColumn() {
        TableColumn column = new TableColumn();
        column.setColumn("com_m_common_key");
        return column;
    }

    public static TableColumn getCreateAtColumn() {
        TableColumn column = new TableColumn();
        column.setColumn("com_m_common_createat");
        return column;
    }
}
