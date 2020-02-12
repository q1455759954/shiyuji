package com.example.administrator.shiyuji.ui.fragment.bean;

import com.example.administrator.shiyuji.util.annotation.PrimaryKey;

import java.io.Serializable;

public class UserBean implements Serializable {

    public UserBean() {
    }

    /**
     * 用户identifier,qq号或者手机号
     */
    @PrimaryKey(column = "idStr")
    private String account;

    /**
     * 微博昵称
     */
    private String screen_name;

    /**
     * 个人描述
     */
    private String description;

    /**
     * 自定义图像
     */
    private String profile_image_url;

    /**
     * 性别,m--男，f--女,n--未知
     */
    private String gender;

    /**
     * 粉丝数
     */
    private Integer followers_count;

    /**
     * 关注数
     */
    private Integer friends_count;

    /**
     * 是否已关注(此特性暂不支持)
     */
    private Boolean following;

    /**
     * 用户的互粉数
     */
    private Integer bi_followers_count;


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(Integer followers_count) {
        this.followers_count = followers_count;
    }

    public Integer getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(Integer friends_count) {
        this.friends_count = friends_count;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public Integer getBi_followers_count() {
        return bi_followers_count;
    }

    public void setBi_followers_count(Integer bi_followers_count) {
        this.bi_followers_count = bi_followers_count;
    }
}
