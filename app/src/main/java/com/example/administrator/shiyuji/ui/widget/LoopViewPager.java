package com.example.administrator.shiyuji.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2019/10/12.
 */

public class LoopViewPager extends ViewPager{
    private OnViewPagerTouchListener mTouchListener= null;
    public LoopViewPager(Context context) {
        super(context);
    }
    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev){
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(mTouchListener != null) {//摸到
                    mTouchListener.onPagerTouch(true);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(mTouchListener != null) {//摸到
                    mTouchListener.onPagerTouch(false);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
    public void setOnViewPagerTouchListener(OnViewPagerTouchListener listener){
        this.mTouchListener= listener;
    }

    public interface OnViewPagerTouchListener{
        void onPagerTouch(boolean isTouch);
    }
}
