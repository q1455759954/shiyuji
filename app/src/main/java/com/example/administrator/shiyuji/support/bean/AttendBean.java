package com.example.administrator.shiyuji.support.bean;

import com.example.administrator.shiyuji.util.annotation.PrimaryKey;

/**
 * Created by Administrator on 2019/9/23.
 */

public class AttendBean {

    @PrimaryKey(column = "targetUid")
    private String targetUid;

    private boolean isAttend;

    public boolean isAttend() {
        return isAttend;
    }

    public void setAttend(boolean attend) {
        isAttend = attend;
    }

    public String getTargetUid() {
        return targetUid;
    }

    public void setTargetUid(String targetUid) {
        this.targetUid = targetUid;
    }
}
