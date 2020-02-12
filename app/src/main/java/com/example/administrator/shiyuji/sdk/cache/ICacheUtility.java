package com.example.administrator.shiyuji.sdk.cache;

import com.example.administrator.shiyuji.util.network.IResult;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.support.setting.Setting;

/**
 * Created by Administrator on 2019/7/8.
 */
public interface ICacheUtility {
    IResult findCacheData(Setting var1, Params var2);

    void addCacheData(Setting var1, Params var2, IResult var3);
}