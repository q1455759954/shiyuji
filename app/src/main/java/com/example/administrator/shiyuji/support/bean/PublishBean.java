package com.example.administrator.shiyuji.support.bean;

import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.util.annotation.PrimaryKey;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.util.network.bean.Params;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Administrator on 2019/7/7.
 */

public class PublishBean implements Serializable {

    public PublishBean() {
    }

    public enum PublishType {

        // 新微博
        status,
        // 新建评论
        commentCreate,
        // 回复评论
        commentReply,
        // 转发微博
        statusRepost,
        //约信息
        appointment,
        //生活
        life

    }

    private String re_txt;


    private PublishType publishType;

    @PrimaryKey(column = "id")
    String id = UUID.randomUUID().toString();

    private AppointmentInfo appointmentInfo;

    private StatusContent statusContent;

    private StatusComment statusComment;

    private LifeInfo lifeInfo;

    String[] pics;

    String text;

    String errorMsg;

    Params params = new Params();

    private String re_rowKey;

    public String getRe_rowKey() {
        return re_rowKey;
    }

    public void setRe_rowKey(String re_rowKey) {
        this.re_rowKey = re_rowKey;
    }

    public String getRe_txt() {
        return re_txt;
    }

    public void setRe_txt(String re_txt) {
        this.re_txt = re_txt;
    }

    public LifeInfo getLifeInfo() {
        return lifeInfo;
    }

    public void setLifeInfo(LifeInfo lifeInfo) {
        this.lifeInfo = lifeInfo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String[] getPics() {
        return pics;
    }

    public void setPics(String[] pics) {
        this.pics = pics;
    }



    public PublishType getPublishType() {
        return publishType;
    }

    public void setPublishType(PublishType publishType) {
        this.publishType = publishType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AppointmentInfo getAppointmentInfo() {
        return appointmentInfo;
    }

    public void setAppointmentInfo(AppointmentInfo appointmentInfo) {
        this.appointmentInfo = appointmentInfo;
    }

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
}
