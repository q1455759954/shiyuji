package com.example.administrator.shiyuji.ui.fragment.bean;

import com.example.administrator.shiyuji.util.annotation.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/7/6.
 */

public class PictureSize implements Serializable {

    private static final long serialVersionUID = -5213202786229561729L;

    @PrimaryKey(column = "url")
    private String url;

    private long size;

    public PictureSize(String url, long size) {
        this.url = url;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
