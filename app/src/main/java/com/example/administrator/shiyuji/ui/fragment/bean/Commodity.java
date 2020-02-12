package com.example.administrator.shiyuji.ui.fragment.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/10/18.
 */

public class Commodity implements Serializable{

    public Commodity() {
    }

    private String row_key;

    private String create_at;

    private String title;

    private String content;

    private String onLineTime;

    private String price;

    private PicUrls[] picUrls;

    private UserInfo userInfo;


    /**
     * 是否下架
     */
    private boolean state = false;

    public PicUrls[] getPicUrls() {
        return picUrls;
    }

    public void setPicUrls(PicUrls[] picUrls) {
        this.picUrls = picUrls;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
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

    public String getOnLineTime() {
        return onLineTime;
    }

    public void setOnLineTime(String onLineTime) {
        this.onLineTime = onLineTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
