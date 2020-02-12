package com.example.administrator.shiyuji.ui.widget.support.download;

import android.content.Context;

import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;

/**
 * Created by Administrator on 2019/7/2.
 */
public interface Downloader {
    byte[] downloadBitmap(Context var1, String var2, ImageConfig var3) throws Exception;
}

