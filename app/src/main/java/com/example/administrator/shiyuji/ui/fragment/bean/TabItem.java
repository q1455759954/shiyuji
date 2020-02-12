package com.example.administrator.shiyuji.ui.fragment.bean;

import java.io.Serializable;

/**
 *  tablelayout的bean
 */

public class TabItem implements Serializable {
    private static final long serialVersionUID = -1162756298239591517L;
    private String type;
    private String title;
    private Serializable tag;

    public TabItem() {
    }

    public TabItem(String type, String title) {
        this.type = type;
        this.title = title;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Serializable getTag() {
        return this.tag;
    }

    public void setTag(Serializable tag) {
        this.tag = tag;
    }
}
