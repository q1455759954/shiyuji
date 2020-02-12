package com.example.administrator.shiyuji.sdk.cache;

import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.support.genereter.KeyGenerator;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.util.network.IResult;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.support.sqlit.SinaDB;

import java.util.List;

/**
 * 缓存具体实现类，在action文件设置的文件名然后通过calss.forName.newInstance实例化
 * Created by Administrator on 2019/7/8.
 */

public class TimelineCacheUtility implements ICacheUtility {


    public static String getCacheKey(Setting action, Params params) {
        String key = "";

//        if (params.containsKey("max_id")){
//            key = action.getDescription() + ":" + action.getValue() + ":" + params.getParameter("max_id");
//        }else if (params.containsKey("since_id")) {
//            key = action.getDescription() + ":" + action.getValue() + ":" + params.getParameter("since_id");
//        }

//        }else {
//            key = action.getDescription() + ":" + action.getValue() + ":"+params.getParameter("count") ;
//        }

//		key += AppContext.getUser().getIdstr();

        if (params.containsKey("user_id")){
            key+=params.getParameter("user_id");
        }
        if (params.containsKey("page"))
            key += params.getParameter("page");

        return KeyGenerator.generateMD5(key);
    }


    @Override
    public IResult findCacheData(Setting action, Params params) {
        String key = getCacheKey(action, params);
        Extra extra = new Extra("cache", key);
        List<StatusContent> statusList = SinaDB.getTimelineDB().select(extra, StatusContent.class);

        if (statusList.size()>0){
            Log.d("缓存：","成功找到缓存！");
            StatusContents statusContents = new StatusContents();
            statusContents.setFromCache(true);
            //这里将account设为固定，以后做完登录验证后再改
            statusContents.setOutofdate(CacheTimeUtils.isOutofdate(key, null));
//            statusContents.setOutofdate(CacheTimeUtils.isOutofdate(key, AppContext.getAccount().getUser()));
            statusContents.setStatuses(statusList);
            return statusContents;
        }

    return null;

    }

    /**
     * action和params生成一个key，与responseObj存入数据库
     * @param action
     * @param params
     * @param responseObj
     */
    @Override
    public void addCacheData(Setting action, Params params, IResult responseObj) {
        String key = getCacheKey(action, params);
        try {
            StatusContents statusContents = (StatusContents)responseObj;
            if (statusContents.getStatuses().size()==0)
                return;

            boolean clear = false;
            //刷新
            if (!TextUtils.isEmpty(params.getParameter("since_id"))){
                int diff = Math.abs(statusContents.getStatuses().size() - AppSettings.getTimelineCount());
                clear = diff <= 3;
            }
            // 加载更多
            else if (!TextUtils.isEmpty(params.getParameter("max_id"))) {
            }
            // 重置
            else {
                clear = true;
            }
            Extra extra = new Extra("cache",key );
            if (clear) {
                SinaDB.getTimelineDB().deleteAll(extra, StatusContent.class);
            }
            SinaDB.getTimelineDB().insert(extra, statusContents.getStatuses());
            //保存缓存时间
            CacheTimeUtils.saveTime(getCacheKey(action, params), AppContext.getAccount().getUserInfo());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
