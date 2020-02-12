package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2019/7/2.
 */

public class MyDrawable extends BitmapDrawable {
    private MyBitmap myBitmap;
    private ImageConfig config;
    private WeakReference<BitmapLoader.MyBitmapLoaderTask> task;

    public MyDrawable(Resources res, MyBitmap myBitmap, ImageConfig config, WeakReference<BitmapLoader.MyBitmapLoaderTask> task) {
        this(res, myBitmap.getBitmap());
        this.myBitmap = myBitmap;
        this.config = config;
        this.task = task;
    }

    public MyBitmap getMyBitmap() {
        return this.myBitmap;
    }

    public void setMyBitmap(MyBitmap myBitmap) {
        this.myBitmap = myBitmap;
    }

    public MyDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    public ImageConfig getConfig() {
        return this.config;
    }

    public void setConfig(ImageConfig config) {
        this.config = config;
    }

    public WeakReference<BitmapLoader.MyBitmapLoaderTask> getTask() {
        return this.task;
    }

    public void setTask(WeakReference<BitmapLoader.MyBitmapLoaderTask> task) {
        this.task = task;
    }
}

