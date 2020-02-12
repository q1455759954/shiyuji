package com.example.administrator.shiyuji.ui.widget.support;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * Created by Administrator on 2019/7/4.
 */

public class DefaultDisplayer implements Displayer {
    public DefaultDisplayer() {
    }

    public void loadCompletedisplay(ImageView imageView, BitmapDrawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    public void loadFailDisplay(ImageView imageView, BitmapDrawable drawable) {
        imageView.setImageDrawable(drawable);
    }
}