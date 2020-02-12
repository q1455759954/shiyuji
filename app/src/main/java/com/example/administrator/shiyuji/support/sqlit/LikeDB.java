package com.example.administrator.shiyuji.support.sqlit;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.cache.Extra;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.bean.UnreadCount;

public class LikeDB {

    public static void update(UnreadCount unreadCount){
        //保存到数据库
        SinaDB.getDB().deleteAll(null,UnreadCount.class);
        SinaDB.getDB().insert(null, unreadCount);
    }

    public static LikeBean get(String statusId) {
        return SinaDB.getDB().selectById(new Extra(String.valueOf(AppContext.getAccount().getUserInfo().getId()), null), LikeBean.class, statusId);
    }

    public static void insert(LikeBean likeBean) {
        SinaDB.getDB().insertOrReplace(new Extra(String.valueOf(AppContext.getAccount().getUserInfo().getId()), null), likeBean);
    }

}
