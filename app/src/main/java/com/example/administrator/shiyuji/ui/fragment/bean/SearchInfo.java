package com.example.administrator.shiyuji.ui.fragment.bean;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchInfo implements Serializable {

    private List<UserInfo> userInfoList = new ArrayList<>();

    private List<StatusContent> contentList = new ArrayList<>();

    private List<AppointmentInfo> appointmentInfoList = new ArrayList<>();

    private List<LifeInfo> commodityInfo = new ArrayList<>();

    private List<LifeInfo> workInfo = new ArrayList<>();


    private String queryStr;

    private int countPerPage = 1;

    public SearchInfo() {
    }

    public String getQueryStr() {
        return queryStr;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    public List<LifeInfo> getCommodityInfo() {
        return commodityInfo;
    }

    public void setCommodityInfo(List<LifeInfo> commodityInfo) {
        this.commodityInfo = commodityInfo;
    }

    public List<LifeInfo> getWorkInfo() {
        return workInfo;
    }

    public void setWorkInfo(List<LifeInfo> workInfo) {
        this.workInfo = workInfo;
    }

    public int getCountPerPage() {
        return countPerPage;
    }

    public void setCountPerPage(int countPerPage) {
        this.countPerPage = countPerPage;
    }

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }

    public List<StatusContent> getContentList() {
        return contentList;
    }

    public void setContentList(List<StatusContent> contentList) {
        this.contentList = contentList;
    }

    public List<AppointmentInfo> getAppointmentInfoList() {
        return appointmentInfoList;
    }

    public void setAppointmentInfoList(List<AppointmentInfo> appointmentInfoList) {
        this.appointmentInfoList = appointmentInfoList;
    }
}
