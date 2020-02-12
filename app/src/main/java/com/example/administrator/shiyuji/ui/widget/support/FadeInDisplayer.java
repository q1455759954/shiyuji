package com.example.administrator.shiyuji.ui.widget.support;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

/**
 * Created by Administrator on 2019/7/2.
 */

public class FadeInDisplayer implements Displayer {
    public FadeInDisplayer() {
    }

    public void loadCompletedisplay(ImageView imageView, BitmapDrawable drawable) {
        if(imageView.getClass().getSimpleName().indexOf("PhotoView") == -1) {
            if(imageView.getDrawable() != null) {
                TransitionDrawable td = new TransitionDrawable(new Drawable[]{imageView.getDrawable(), drawable});
                imageView.setImageDrawable(td);
                td.startTransition(300);
            } else {
                imageView.setImageDrawable(drawable);
            }

        }
    }

    public void loadFailDisplay(ImageView imageView, BitmapDrawable drawable) {
        imageView.setImageDrawable(drawable);
    }
}
