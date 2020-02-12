package com.example.administrator.shiyuji.ui.fragment.bean;

import java.io.Serializable;

/**
 * 当只显示一个图片时有效
 * Created by Administrator on 2019/7/2.
 */

public class PicSize implements Serializable {

    private static final long serialVersionUID = -2046887085192115167L;

    private int width;

    private int height;

    private String key;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
