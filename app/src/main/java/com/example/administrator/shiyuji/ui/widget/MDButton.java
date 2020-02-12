package com.example.administrator.shiyuji.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.widget.md.MDHelper;

/**
 * Created by Administrator on 2019/7/4.
 */

public class MDButton extends TextView {
    public MDButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs, 0, 0);
    }

    public MDButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public MDButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Drawable d = MDHelper.resolveDrawable(context, R.attr.MDbuttonSelector);
        this.setDefaultSelector(d);
        int themeColor = MDHelper.resolveColor(context, R.attr.colorPrimary);
        ColorStateList colorStateList = getMDTextStateList(context, themeColor);
        this.setTextColor(colorStateList);
    }

    public void setDefaultSelector(Drawable d) {
        MDHelper.setBackgroundCompat(this, d);
    }

    public static ColorStateList getMDTextStateList(Context context, int newPrimaryColor) {
        int fallBackButtonColor = MDHelper.resolveColor(context, 16842806);
        if(newPrimaryColor == 0) {
            newPrimaryColor = fallBackButtonColor;
        }

        int[][] states = new int[][]{{-16842910}, new int[0]};
        int[] colors = new int[]{MDHelper.adjustAlpha(newPrimaryColor, 0.4F), newPrimaryColor};
        return new ColorStateList(states, colors);
    }
}

