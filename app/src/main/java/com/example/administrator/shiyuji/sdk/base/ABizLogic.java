package com.example.administrator.shiyuji.sdk.base;

import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.shiyuji.sdk.cache.ResultBean;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.util.network.IResult;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.sdk.cache.ICacheUtility;
import com.example.administrator.shiyuji.sdk.http.DefHttpUtility;
import com.example.administrator.shiyuji.sdk.http.HttpConfig;
import com.example.administrator.shiyuji.sdk.http.IHttpUtility;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.support.setting.SettingExtra;
import com.example.administrator.shiyuji.support.setting.SettingUtility;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * http连接的基类，设置了缓存的workTask，以及获取数据的逻辑处理（缓存，http）
 * 具体的http获取数据在DefHttpUtility类中实现
 * Created by Administrator on 2019/7/8.
 */

public abstract class ABizLogic implements IHttpUtility {
    public static final String HTTP_UTILITY = "http";
    public static final String BASE_URL = "base_url";

    private ABizLogic.CacheMode mCacheMode;

    public static final Executor CACHE_THREAD_POOL_EXECUTOR;//加入缓存的任务线程池
    static final ThreadFactory sThreadFactory = new ThreadFactory() {//构造线程池的线程工厂
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "BizlogicCacheTask #" + this.mCount.getAndIncrement());
        }
    };
    static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(10);//构造线程池的堵塞队列
    protected abstract HttpConfig configHttpConfig();

    public ABizLogic() {
        this.mCacheMode = ABizLogic.CacheMode.disable;
    }

    public ABizLogic(ABizLogic.CacheMode cacheMode) {
        this.mCacheMode = cacheMode;
    }

    public <T> T doGet(Setting actionSetting, Params params, Class<T> responseCls) throws TaskException {
        return this.doGet(this.configHttpConfig(), actionSetting, params, responseCls);
    }

    public <T> T doPost(HttpConfig config, Setting action, Params urlParams, Params bodyParams, Object requestObj, Class<T> responseCls) throws TaskException {
        long time = System.currentTimeMillis();
        Object result = this.getHttpUtility(action).doPost(this.resetHttpConfig(config, action), action, urlParams, bodyParams, requestObj, responseCls);
        return (T) result;
    }

    public <T> T doPostFiles(HttpConfig config, Setting action, Params urlParams, Params bodyParams, MultipartFile[] files, Class<T> responseCls) throws TaskException {
        Object result = this.getHttpUtility(action).doPostFiles(this.resetHttpConfig(config, action), action, urlParams, bodyParams, files, responseCls);
        return (T) result;
    }


    /**
     *
     * @param config    cookie ,url
     * @param action    xml里的配置
     * @param params    SinaSDK 中的friendshipGroupsTimeline 有写。动态id，条数
     * @param responseCls   将结果封装到哪个类中
     * @param <T>
     * @return
     * @throws TaskException
     */
    @Override
    public <T> T doGet(HttpConfig config, Setting action, Params params, Class<T> responseCls) throws TaskException {
        ICacheUtility cacheUtility = null;
        IResult cache = null;
        if(action.getExtras().containsKey("cache_utility")) {
            if(!TextUtils.isEmpty(((SettingExtra)action.getExtras().get("cache_utility")).getValue())) {
                try {
                    //通过在action文件里设置的value得到缓存实体类
                    cacheUtility = (ICacheUtility)Class.forName(((SettingExtra)action.getExtras().get("cache_utility")).getValue()).newInstance();
                } catch (Exception var10) {
                    Log.w("ABizLogic", "CacheUtility 配置错误");
                }
            }
        } else {
            Log.v("ABizLogic", "CacheUtility 没有配置");
        }
        //查找缓存
        if(this.mCacheMode != ABizLogic.CacheMode.disable && cacheUtility != null) {
            Log.d("缓存","正在查找缓存是否存在！");
            cache = cacheUtility.findCacheData(action, params);
        }
        //缓存不为空则返回缓存，为空http获取
        if(cache != null && this.mCacheMode != ABizLogic.CacheMode.servicePriority) {
            ResultBean resultBean = (ResultBean)cache;
            resultBean.setFromCache(true);
            return (T) resultBean;
        }else {
            try {
                Object result = this.getHttpUtility(action).doGet(this.resetHttpConfig(config, action), action, params, responseCls);
                //将结果放入缓存
                if(result != null && result instanceof IResult) {
                    Log.d("缓存","查找数据成功，正在加入缓存!");
                    this.putToCache(action, params, (IResult)result, cacheUtility);
                }
                return (T) result;
            } catch (TaskException var11) {
                throw var11;
            } catch (Exception var12) {
                throw new TaskException(TextUtils.isEmpty(var12.getMessage())?"服务器错误":var12.getMessage());
            }
        }

    }

    static {
        CACHE_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(10, 128, 1L, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    }
    /**
     * 加入到缓存中
     * @param setting
     * @param params
     * @param data
     * @param cacheUtility
     */
    public void putToCache(Setting setting, Params params, IResult data, ICacheUtility cacheUtility) {
        if(data != null && cacheUtility != null && !data.fromCache()) {
            (new ABizLogic.PutCacheTask(setting, params, data, cacheUtility)).executeOnExecutor(CACHE_THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    /**
     * 重新设置baseUrl
     * @param config
     * @param actionSetting
     * @return
     */
    private HttpConfig resetHttpConfig(HttpConfig config, Setting actionSetting) {
        try {
            if(actionSetting != null && actionSetting.getExtras().containsKey("base_url")) {
                config.baseUrl = ((SettingExtra)actionSetting.getExtras().get("base_url")).getValue().toString();
            }
        } catch (Exception var4) {
            ;
        }
        return config;
    }

    /**
     * 返回一个HttpUtility
     * @param action
     * @return
     */
    private IHttpUtility getHttpUtility(Setting action) {
//        if(action.getExtras().get("http") != null && !TextUtils.isEmpty(((SettingExtra)action.getExtras().get("http")).getValue())) {
//            try {
//                IHttpUtility e = (IHttpUtility)Class.forName(((SettingExtra)action.getExtras().get("http")).getValue()).newInstance();
//                return e;
//            } catch (Exception var3) {
//                var3.printStackTrace();
////                Logger.w("BizLogic", "CacheUtility 没有配置或者配置错误");
//            }
//        }

        return this.configHttpUtility();
    }

    protected IHttpUtility configHttpUtility() {
        try {
            if(!TextUtils.isEmpty(SettingUtility.getStringSetting("http"))) {
                return (IHttpUtility)Class.forName(SettingUtility.getStringSetting("http")).newInstance();
            }
        } catch (Exception var2) {
//            Logger.printExc(ABizLogic.class, var2);
        }

        return new DefHttpUtility();
    }

    public static Setting getSetting(String type) {
        return SettingUtility.getSetting(type);
    }

    protected SettingExtra newSettingExtra(String type, String value, String desc) {
        SettingExtra extra = new SettingExtra();
        extra.setType(type);
        extra.setValue(value);
        extra.setDescription(desc);
        return extra;
    }



    class PutCacheTask extends WorkTask<Void, Void, Void> {
        private Setting setting;
        private Params params;
        private IResult o;
        private ICacheUtility cacheUtility;

        PutCacheTask(Setting setting, Params params, IResult o, ICacheUtility cacheUtility) {
            this.setting = setting;
            this.params = params;
            this.o = o;
            this.cacheUtility = cacheUtility;
        }

        public Void workInBackground(Void... p) throws TaskException {
            long time = System.currentTimeMillis();
            this.cacheUtility.addCacheData(this.setting, this.params, this.o);
            return null;
        }
    }
    public static enum CacheMode {
        auto,
        servicePriority,
        cachePriority,
        disable;

        private CacheMode() {
        }
    }
}
