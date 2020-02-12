package com.example.administrator.shiyuji.ui.widget.support;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * Created by Administrator on 2019/7/2.
 */
public interface Displayer {
    void loadCompletedisplay(ImageView var1, BitmapDrawable var2);

    void loadFailDisplay(ImageView var1, BitmapDrawable var2);
}
