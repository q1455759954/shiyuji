package com.example.administrator.shiyuji.support.bean;

import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.util.annotation.PrimaryKey;

/**
 * Created by Administrator on 2019/8/6.
 */

public class AccountBean {

    @PrimaryKey(column = "uid")
    private String uid;

    private String account;

    /**
     * 用户授权token
     */
    private AccessToken accessToken;

    /**
     * tuikit的验证，（最好服务端生成返回，但是尝试服务端生成时Base64Url找不到，暂时客户端生成保存）
     */
    private String userSig;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * 未读消息
     */
    private UnreadCount unreadCount;

    public AccountBean() {
    }

    public UnreadCount getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(UnreadCount unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public AccountBean(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

//    public UserInfo getUser() {
//        return userInfo;
//    }
//
//    public void setUser(UserInfo userInfo) {
//        this.userInfo = userInfo;
//    }
}
