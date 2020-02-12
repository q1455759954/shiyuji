package com.example.administrator.shiyuji.support.sqlit.extra;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2019/7/8.
 */

public class TableColumn {
    private String dataType;
    private Field field;//字段
    private String column;//列名
    private String columnType;//列类型
    private String columnValue;//值

    public TableColumn() {
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Field getField() {
        return this.field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getColumnType() {
        return this.columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnValue() {
        return this.columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }
}
