package com.example.administrator.shiyuji.util.task;

/**
 * Created by Administrator on 2019/6/28.
 */


public interface IExceptionDeclare {
    void checkResponse(String var1) throws TaskException;

    String checkCode(String var1);
}