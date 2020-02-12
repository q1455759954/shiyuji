package com.example.administrator.shiyuji.util.common;

import android.app.Application;
import android.os.Handler;

import com.squareup.okhttp.OkHttpClient;

import org.litepal.LitePalApplication;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/6/28.
 */


public class GlobalContext extends LitePalApplication {
    private static GlobalContext _context;
    public static final int CONN_TIMEOUT = 30000;
    public static final int READ_TIMEOUT = 30000;
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    Handler mHandler = new Handler() {
    };

    public GlobalContext() {
    }

    public void onCreate() {
        super.onCreate();
        _context = this;
    }

    public static GlobalContext getInstance() {
        return _context;
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static void configOkHttpClient(int connTimeout, int socketTimeout) {
        if(mOkHttpClient != null) {
            mOkHttpClient.setConnectTimeout((long)connTimeout, TimeUnit.MILLISECONDS);
            mOkHttpClient.setReadTimeout((long)socketTimeout, TimeUnit.MILLISECONDS);
        }

    }

    static {
        configOkHttpClient(30000, 30000);
    }
}

