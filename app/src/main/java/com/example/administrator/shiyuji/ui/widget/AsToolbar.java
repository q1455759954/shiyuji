package com.example.administrator.shiyuji.ui.widget;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;

/**
 * Created by Administrator on 2019/7/4.
 */

public class AsToolbar extends Toolbar {
    static final String TAG = "AsToolbar";
    private long lastClickTime = 0L;

    public AsToolbar(Context context) {
        super(context);
    }

    public AsToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AsToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean handler = super.onTouchEvent(ev);
        if(ev.getAction() == 1) {
            if(this.lastClickTime != 0L && System.currentTimeMillis() - this.lastClickTime <= 500L) {
                BaseActivity activity = BaseActivity.getRunningActivity();
                if(activity != null && activity instanceof AsToolbar.OnToolbarDoubleClick) {
                    activity.onToolbarDoubleClick();
                }
            }

            this.lastClickTime = System.currentTimeMillis();
        }

        return handler;
    }

    public void performDoublcClick() {
        BaseActivity activity = BaseActivity.getRunningActivity();
        if(activity != null && activity instanceof AsToolbar.OnToolbarDoubleClick) {
            activity.onToolbarDoubleClick();
        }

    }

    public interface OnToolbarDoubleClick {
        boolean onToolbarDoubleClick();
    }
}
