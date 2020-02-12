package com.example.administrator.shiyuji.ui.fragment.bean;

import com.example.administrator.shiyuji.sdk.cache.ResultBean;

import java.io.Serializable;
import java.util.List;

public class StatusComments extends ResultBean implements Serializable {

    private List<StatusComment> comments;

    public StatusComments() {

    }

    public List<StatusComment> getComments() {
        return comments;
    }

    public void setComments(List<StatusComment> comments) {
        this.comments = comments;
    }

}
