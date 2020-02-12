package com.example.administrator.shiyuji.ui.fragment.bean;

import java.io.Serializable;

public class VideoInfo implements Serializable {

    public VideoInfo() {
    }

    private String title;

    private String pic_url;

    private String video_url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
