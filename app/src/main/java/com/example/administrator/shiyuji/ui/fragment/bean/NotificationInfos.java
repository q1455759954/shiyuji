package com.example.administrator.shiyuji.ui.fragment.bean;

import android.app.Notification;

import com.example.administrator.shiyuji.sdk.cache.ResultBean;
import com.example.administrator.shiyuji.util.network.IResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019/9/27.
 */

public class NotificationInfos extends ResultBean implements Serializable{

    private List<NotificationInfo> notificationList;

    public NotificationInfos() {

    }

    public List<NotificationInfo> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<NotificationInfo> notificationList) {
        this.notificationList = notificationList;
    }
}
