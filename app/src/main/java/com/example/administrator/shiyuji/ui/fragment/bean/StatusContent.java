package com.example.administrator.shiyuji.ui.fragment.bean;


import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.util.annotation.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

public class StatusContent implements Serializable, BizFragment.ILikeBean {

    /**
     * 创建时间
     */
    private String created_at;

    /**
     * 微博ID
     */
    @PrimaryKey(column = "id")
    private String row_key;

    /**
     * 微博信息内容
     */
    private String text;

    /**
     * 类别
     */
    private String type;

    /**
     * 是否点赞
     */
    private Boolean favorited;

    /**
     * 是否收藏
     */
    private Boolean collect;


    /**
     * 图片配图，多图时，返回多图链接
     */
    private PicUrls[] pic_urls;

    /**
     * 作者信息
     */
    private UserBean user;

    private int userId;//暂时使用这个

    private UserInfo userInfo;//暂时使用这个

    /**
     * 转发的博文，内容为status，如果不是转发，则没有此字段
     */
    private StatusContent retweeted_status;

    /**
     * 转发数量
     */
    private int reposts_count;

    /**
     * 评论数量
     */
    private int comments_count;

    /**
     * 表态数
     */
    private int attitudes_count;

    /**
     * 判断是热门还是个人主页的
     */
    private boolean isHotStatus = true;


    /**
     * 视频
     */
    private VideoInfo videoInfo;

    /**
     * 收藏或通知的rowkey
     */
    private String re_row_key;

    public String getRe_row_key() {
        return re_row_key;
    }

    public void setRe_row_key(String re_row_key) {
        this.re_row_key = re_row_key;
    }

    public Boolean getCollect() {
        return collect;
    }

    public void setCollect(Boolean collect) {
        this.collect = collect;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public boolean isHotStatus() {
        return isHotStatus;
    }

    public void setHotStatus(boolean hotStatus) {
        isHotStatus = hotStatus;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
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

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PicUrls[] getPic_urls() {
        return pic_urls;
    }

    public void setPic_urls(PicUrls[] pic_urls) {
        this.pic_urls = pic_urls;
    }

//    public UserBean getUser() {
//        return user;
//    }
//
//    public void setUser(UserBean user) {
//        this.user = user;
//    }

    public StatusContent getRetweeted_status() {
        return retweeted_status;
    }

    public void setRetweeted_status(StatusContent retweeted_status) {
        this.retweeted_status = retweeted_status;
    }

    public int getReposts_count() {
        return reposts_count;
    }

    public void setReposts_count(int reposts_count) {
        this.reposts_count = reposts_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getAttitudes_count() {
        return attitudes_count;
    }

    public void setAttitudes_count(int attitudes_count) {
        this.attitudes_count = attitudes_count;
    }

    @Override
    public String getLikeId() {
        return getRow_key();
    }
}
