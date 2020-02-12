package com.example.administrator.shiyuji.util.task;

/**
 * Created by Administrator on 2019/6/28.
 */

public interface ITaskManager {
    void addTask(WorkTask var1);

    void removeTask(String var1, boolean var2);

    void removeAllTask(boolean var1);

    int getTaskCount(String var1);
}
