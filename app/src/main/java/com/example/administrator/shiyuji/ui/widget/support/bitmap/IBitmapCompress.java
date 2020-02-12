package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;

import java.io.File;

/**
 * Created by Administrator on 2019/7/2.
 */
public interface IBitmapCompress {
    MyBitmap compress(byte[] var1, File var2, String var3, ImageConfig var4, int var5, int var6) throws Exception;
}