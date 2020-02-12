package com.example.administrator.shiyuji.util.task;

import android.os.Bundle;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by Administrator on 2019/6/28.
 */

public class TaskManager implements ITaskManager {
    static final String TAG = "TaskManager";
    private LinkedHashMap<String, WeakReference<WorkTask>> taskCache = new LinkedHashMap();
    private HashMap<String, Integer> taskCountMap = new HashMap();

    public TaskManager() {
    }

    public void addTask(WorkTask task) {
        if(task != null && !TextUtils.isEmpty(task.getTaskId())) {
            int count = this.taskCountMap.keySet().contains(task.getTaskId())?((Integer)this.taskCountMap.get(task.getTaskId())).intValue():0;
            HashMap var10000 = this.taskCountMap;
            String var10001 = task.getTaskId();
            ++count;
            var10000.put(var10001, Integer.valueOf(count));
            this.cancelExistTask(task.getTaskId(), true);
            this.taskCache.put(task.getTaskId(), new WeakReference(task));
//            Logger.d("TaskManager", String.format("addTask() --->%s", new Object[]{task.getTaskId()}));
        }

    }

    public void removeTask(String taskId, boolean cancelIfRunning) {
        this.cancelExistTask(taskId, cancelIfRunning);
    }

    public void removeAllTask(boolean mayInterruptIfRunning) {
        Set keySet = this.taskCache.keySet();
        Iterator var3 = keySet.iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            WorkTask task = this.getTaskById(key);
            if(task != null) {
                task.cancel(mayInterruptIfRunning);
            }
        }

        this.taskCache.clear();
    }

    private void cancelExistTask(String taskId, boolean mayInterruptIfRunning) {
        WorkTask existTask = this.getTaskById(taskId);
        if(existTask != null) {
//            Logger.d("TaskManager", String.format("interrupt exist task --->%s", new Object[]{taskId}));
        }

        if(existTask != null) {
            existTask.cancel(mayInterruptIfRunning);
        }

        this.taskCache.remove(taskId);
    }

    private WorkTask getTaskById(String taskId) {
        WeakReference existTaskRef = (WeakReference)this.taskCache.get(taskId);
        return existTaskRef != null?(WorkTask)existTaskRef.get():null;
    }

    public int getTaskCount(String taskId) {
        return TextUtils.isEmpty(taskId)?0:(this.taskCountMap.keySet().contains(taskId)?((Integer)this.taskCountMap.get(taskId)).intValue():0);
    }

    public void cleatTaskCount(String taskId) {
        if(!TextUtils.isEmpty(taskId)) {
            this.taskCountMap.remove(taskId);
        }

    }

    public void save(Bundle outState) {
        outState.putSerializable("map", this.taskCountMap);
    }

    public void restore(Bundle savedInstanceState) {
        if(savedInstanceState.getSerializable("map") != null) {
            this.taskCountMap = (HashMap)savedInstanceState.getSerializable("map");
        }

    }
}
