package com.example.administrator.shiyuji.ui.fragment.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/9/27.
 */

public class NotificationInfo implements Serializable{

    public NotificationInfo() {
    }


    /**
     * 通知的row-key
     */
    private String row_key;

    /**
     * 当前用户的信息
     */
    private UserInfo userInfo;

    /**
     * 通知发起人
     */
    private UserInfo targetUser;

    /**
     * 类型，11点赞动态，12点赞评论，13点赞回复，21评论动态，22评论评论（就是回复），23评论回复（就是回复回复），31艾特动态，32艾特评论，33艾特回复
     */
    private int type;

    /**
     * 评论、回复内容
     */
    private String text;

    /**
     * 通知的时间
     */
    private String create_at;

    /**
     * 评论的动态
     */
    private StatusContent statusContent;

    /**
     * 评论的评论
     */
    private StatusComment statusComment;

    public StatusContent getStatusContent() {
        return statusContent;
    }

    public void setStatusContent(StatusContent statusContent) {
        this.statusContent = statusContent;
    }

    public StatusComment getStatusComment() {
        return statusComment;
    }

    public void setStatusComment(StatusComment statusComment) {
        this.statusComment = statusComment;
    }

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(UserInfo targetUser) {
        this.targetUser = targetUser;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }
}
