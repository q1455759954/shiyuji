package com.example.administrator.shiyuji.ui.fragment.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/10/18.
 */

public class WorkInfo implements Serializable{

    public WorkInfo() {
    }

    private String row_key;

    private String title;

    private String content;

    private UserInfo userInfo;

    /**
     * 通知的时间
     */
    private String create_at;

    /**
     * 是否下架
     */
    private boolean state;

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
