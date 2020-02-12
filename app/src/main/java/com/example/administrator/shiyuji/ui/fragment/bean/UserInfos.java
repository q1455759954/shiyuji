package com.example.administrator.shiyuji.ui.fragment.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/12/7.
 */

public class UserInfos implements Serializable{

    private List<UserInfo> userInfoList;

    public UserInfos() {
    }

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }
}
