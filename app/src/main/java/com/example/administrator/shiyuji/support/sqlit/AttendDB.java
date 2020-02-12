package com.example.administrator.shiyuji.support.sqlit;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.cache.Extra;
import com.example.administrator.shiyuji.support.bean.AttendBean;
import com.example.administrator.shiyuji.support.bean.LikeBean;

/**
 * Created by Administrator on 2019/9/23.
 */

public class AttendDB {

    public static AttendBean get(String statusId) {
        return SinaDB.getDB().selectById(new Extra(String.valueOf(AppContext.getAccount().getUserInfo().getId()), null), AttendBean.class, statusId);
    }

    public static void insert(AttendBean attendBean) {
        SinaDB.getDB().insertOrReplace(new Extra(String.valueOf(AppContext.getAccount().getUserInfo().getId()), null), attendBean);
    }

}
