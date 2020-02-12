package com.example.administrator.shiyuji.util.widgetutil;

import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2019/9/11.
 */

public class WidgetUtil {

    /**
     * 通过反射机制 修改TabLayout 的下划线长度
     */
    public static void setIndicator (TabLayout tabs, int leftDip, int rightDip) {
        //通过反射获取到
        Class tabLayout = tabs.getClass();
        Field tabStrip =null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        }catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        //设置模式
        tabStrip.setAccessible(true);
        //获得tabview
        LinearLayout llTab =null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //设置tabView的padding为0，并且设置了margin
        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());
        for (int i =0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

}
