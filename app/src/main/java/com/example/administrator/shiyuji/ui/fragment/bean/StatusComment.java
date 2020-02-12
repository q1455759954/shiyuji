package com.example.administrator.shiyuji.ui.fragment.bean;

import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapTask;
import com.example.administrator.shiyuji.util.annotation.PrimaryKey;

import java.io.Serializable;

public class StatusComment implements Serializable, BizFragment.ILikeBean{


    /**
     * 创建时间
     */
    private String created_at;
    /**
     * 评论ID
     */
    @PrimaryKey(column = "id")
    private String row_key;

    /**
     * 评论内容
     */
    private String text;

    /**
     * 原动态行键
     */
    private String status_row_key;

    /**
     * 作者信息
     */
    private UserInfo userInfo;

    /**
     * 评论数量
     */
    private int response_count;

    /**
     * 表态数
     */
    private int attitudes_count;


    /**
     * 回复的评论信息字段
     */
    private StatusComment reply_comment;

    /**
     * @的用户
     */
    private String ATUser;

    /**
     * 回复或评论
     */
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getATUser() {
        return ATUser;
    }

    public void setATUser(String ATUser) {
        this.ATUser = ATUser;
    }

    private long likedCount;

    private boolean picture;

    private boolean liked;

    public String getStatus_row_key() {
        return status_row_key;
    }

    public void setStatus_row_key(String status_row_key) {
        this.status_row_key = status_row_key;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public int getResponse_count() {
        return response_count;
    }

    public void setResponse_count(int response_count) {
        this.response_count = response_count;
    }

    public int getAttitudes_count() {
        return attitudes_count;
    }

    public void setAttitudes_count(int attitudes_count) {
        this.attitudes_count = attitudes_count;
    }

    public StatusComment getReply_comment() {
        return reply_comment;
    }

    public void setReply_comment(StatusComment reply_comment) {
        this.reply_comment = reply_comment;
    }

    public long getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(long likedCount) {
        this.likedCount = likedCount;
    }

    public boolean isPicture() {
        return picture;
    }

    public void setPicture(boolean picture) {
        this.picture = picture;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    @Override
    public String getLikeId() {
        return getRow_key();
    }
}
