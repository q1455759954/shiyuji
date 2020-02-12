package com.example.administrator.shiyuji.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2019/7/9.
 */


public class AspectImageView extends ImageView {
    public AspectImageView(Context context) {
        super(context);
    }

    public AspectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desireWidth;
        float aspect;

        Drawable drawable = getDrawable();
        if (drawable == null) {
            desireWidth = 0;
            aspect = 1;
        } else {
            desireWidth = drawable.getIntrinsicWidth();
            aspect = drawable.getIntrinsicWidth() * 1.0f / drawable.getIntrinsicHeight();
        }

        //获取期望值,基于MeasureSpec
        int widthSize = View.resolveSize(desireWidth, widthMeasureSpec);
        int heightSize = (int) (widthSize / aspect);

        //检查heightSize不要太大
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        if (mode == MeasureSpec.EXACTLY || mode == MeasureSpec.AT_MOST) {
            if (heightSize > size) {
                heightSize = size;
                widthSize = (int) (aspect * heightSize);
            }
        }

        setMeasuredDimension((widthSize*7)/10, (heightSize*7)/10);
    }
}