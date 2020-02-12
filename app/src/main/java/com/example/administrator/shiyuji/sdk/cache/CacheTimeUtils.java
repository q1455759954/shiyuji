package com.example.administrator.shiyuji.sdk.cache;

import com.example.administrator.shiyuji.ui.activity.base.ActivityHelper;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.common.GlobalContext;

/**
 * Created by Administrator on 2019/9/1.
 */

class CacheTimeUtils {


    /**
     * 保存动态缓存时间
     * @param key
     * @param owner
     */
    public static void saveTime(String key, UserInfo owner) {
        if (owner != null)
            key = String.format("%s-%s", key, owner.getIdentifier());

        String time = String.valueOf(System.currentTimeMillis() / 1000);

        ActivityHelper.putShareData(GlobalContext.getInstance(), key, time);

//        Logger.d("CacheTimeUtils", String.format("保存缓存 %s, saveTime = %s", key, time));
    }

    /**
     * 判断动态是否过期
     * @param cacheKey
     * @param user
     * @return
     */
    public static boolean isOutofdate(String cacheKey, UserInfo user) {
        if (user != null)
            cacheKey = String.format("%s-%s", cacheKey, user.getIdentifier());

        long saveTime = Long.parseLong(ActivityHelper.getShareData(GlobalContext.getInstance(), cacheKey, "0"));

        boolean expired = (System.currentTimeMillis() / 1000 - saveTime) * 1000 >= AppSettings.getRefreshInterval();

        return expired;
    }
}
