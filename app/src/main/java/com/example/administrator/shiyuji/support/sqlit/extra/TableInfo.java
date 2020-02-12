package com.example.administrator.shiyuji.support.sqlit.extra;

import com.example.administrator.shiyuji.util.annotation.AutoIncrementPrimaryKey;
import com.example.administrator.shiyuji.util.annotation.ColumnField;
import com.example.administrator.shiyuji.util.annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/7/8.
 */

public class TableInfo {
    private Class<?> clazz;
    private TableColumn primaryKey;
    private String tableName;
    private final List<TableColumn> columns = new ArrayList();

    public TableInfo(Class<?> clazz) {
        this.clazz = clazz;
        this.setInit();
    }

    public TableInfo(String tableName, TableColumn primaryKey) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
    }

    private <T> void setInit() {
        this.setTableName();
        this.setColumns(this.clazz);
        if(this.primaryKey == null) {
            throw new RuntimeException("类 " + this.clazz.getSimpleName() + " 没有设置主键，请使用标注主键");
        } else {
//            if(Logger.DEBUG) {
//                Logger.v("SqliteUtility", String.format("类 %s 的主键是 %s", new Object[]{this.clazz.getSimpleName(), this.primaryKey.getColumn()}));
//                Iterator var1 = this.columns.iterator();
//
//                while(var1.hasNext()) {
//                    TableColumn column = (TableColumn)var1.next();
//                    Logger.v("SqliteUtility", String.format("[column = %s, datatype = %s]", new Object[]{column.getColumn(), column.getDataType()}));
//                }
//            }

        }
    }

    private void setTableName() {
        this.tableName = TableInfoUtils.getTableName(this.clazz);
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public TableColumn getPrimaryKey() {
        return this.primaryKey;
    }

    public String getTableName() {
        return this.tableName;
    }

    public List<TableColumn> getColumns() {
        return this.columns;
    }

    public void setColumns(Class<?> c) {
        if(c != null && !"Object".equalsIgnoreCase(c.getSimpleName())) {
            Field[] fields = c.getDeclaredFields();
            Field[] var3 = fields;
            int var4 = fields.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Field field = var3[var5];
                if(!"serialVersionUID".equals(field.getName()) && !"$change".equals(field.getName())) {
                    ColumnField columnField = (ColumnField)field.getAnnotation(ColumnField.class);
                    if(columnField == null || columnField.serialize()) {
                        if(this.primaryKey == null) {
                            PrimaryKey column = (PrimaryKey)field.getAnnotation(PrimaryKey.class);
                            if(column != null) {
                                this.primaryKey = new TableColumn();
                                this.primaryKey.setColumn(column.column());
                                this.setColumn(field, this.primaryKey);
                                continue;
                            }

                            AutoIncrementPrimaryKey autoIncrementPrimaryKey = (AutoIncrementPrimaryKey)field.getAnnotation(AutoIncrementPrimaryKey.class);
                            if(autoIncrementPrimaryKey != null) {
                                this.primaryKey = new AutoIncrementTableColumn();
                                this.primaryKey.setColumn(autoIncrementPrimaryKey.column());
                                this.setColumn(field, this.primaryKey);
                                continue;
                            }
                        }

                        TableColumn var10 = new TableColumn();
                        var10.setColumn(field.getName());
                        this.setColumn(field, var10);
                        this.columns.add(var10);
                    }
                }
            }

            this.setColumns(c.getSuperclass());
        }
    }

    private void setColumn(Field field, TableColumn column) {
        column.setField(field);
        if(!field.getType().getName().equals("int") && !field.getType().getName().equals("java.lang.Integer")) {
            if(!field.getType().getName().equals("long") && !field.getType().getName().equals("java.lang.Long")) {
                if(!field.getType().getName().equals("float") && !field.getType().getName().equals("java.lang.Float")) {
                    if(!field.getType().getName().equals("double") && !field.getType().getName().equals("java.lang.Double")) {
                        if(!field.getType().getName().equals("boolean") && !field.getType().getName().equals("java.lang.Boolean")) {
                            if(!field.getType().getName().equals("char") && !field.getType().getName().equals("java.lang.Character")) {
                                if(!field.getType().getName().equals("byte") && !field.getType().getName().equals("java.lang.Byte")) {
                                    if(!field.getType().getName().equals("short") && !field.getType().getName().equals("java.lang.Short")) {
                                        if(field.getType().getName().equals("java.lang.String")) {
                                            column.setDataType("string");
                                            column.setColumnType("TEXT");
                                        } else if(field.getType().getName().equals("[B")) {
                                            column.setDataType("blob");
                                            column.setColumnType("BLOB");
                                        } else {
                                            column.setDataType("object");
                                            column.setColumnType("TEXT");
                                        }
                                    } else {
                                        column.setDataType("short");
                                        column.setColumnType("TEXT");
                                    }
                                } else {
                                    column.setDataType("byte");
                                    column.setColumnType("INTEGER");
                                }
                            } else {
                                column.setDataType("char");
                                column.setColumnType("TEXT");
                            }
                        } else {
                            column.setDataType("boolean");
                            column.setColumnType("TEXT");
                        }
                    } else {
                        column.setDataType("double");
                        column.setColumnType("REAL");
                    }
                } else {
                    column.setDataType("float");
                    column.setColumnType("REAL");
                }
            } else {
                column.setDataType("long");
                column.setColumnType("INTEGER");
            }
        } else {
            column.setDataType("int");
            column.setColumnType("INTEGER");
        }

    }
}
