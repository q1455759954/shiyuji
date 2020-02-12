package com.example.administrator.shiyuji.ui.widget.md;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.example.administrator.shiyuji.R;

/**
 * Created by Administrator on 2019/7/4.
 */

public class MDHelper {
    public MDHelper() {
    }

    public static void setRadioButtonTint(RadioButton radioButton, int color) {
        ColorStateList sl = new ColorStateList(new int[][]{{-16842912}, {16842912}}, new int[]{resolveColor(radioButton.getContext(), 16842808), color});
        if(Build.VERSION.SDK_INT >= 21) {
            radioButton.setButtonTintList(sl);
        } else {
            Drawable drawable = ContextCompat.getDrawable(radioButton.getContext(), R.drawable.abc_btn_radio_material);
            DrawableWrapper d = new DrawableWrapper(drawable);
            d.setTintList(sl);
            radioButton.setButtonDrawable(d);
        }

    }

    public static void setProgressBarTint(ProgressBar progressBar, int color) {
        if(Build.VERSION.SDK_INT >= 21) {
            ColorStateList mode = ColorStateList.valueOf(color);
            progressBar.setProgressTintList(mode);
            progressBar.setSecondaryProgressTintList(mode);
            progressBar.setIndeterminateTintList(mode);
        } else {
            PorterDuff.Mode mode1 = PorterDuff.Mode.SRC_IN;
            if(Build.VERSION.SDK_INT <= 10) {
                mode1 = PorterDuff.Mode.MULTIPLY;
            }

            if(progressBar.getIndeterminateDrawable() != null) {
                progressBar.getIndeterminateDrawable().setColorFilter(color, mode1);
            }

            if(progressBar.getProgressDrawable() != null) {
                progressBar.getProgressDrawable().setColorFilter(color, mode1);
            }
        }

    }

    public static void setEditTextTint(EditText editText, int color) {
        ColorStateList s1 = ColorStateList.valueOf(color);
        if(Build.VERSION.SDK_INT >= 21) {
            editText.setBackgroundTintList(s1);
        } else {
            Drawable drawable = ContextCompat.getDrawable(editText.getContext(), R.drawable.abc_edit_text_material);
            DrawableWrapper d = new DrawableWrapper(drawable);
            d.setTintList(s1);
            setBackgroundCompat(editText, d);
        }

    }

    public static void setCheckBoxTint(CheckBox box, int color) {
        ColorStateList sl = new ColorStateList(new int[][]{{-16842912}, {16842912}}, new int[]{resolveColor(box.getContext(), 16842808), color});
        if(Build.VERSION.SDK_INT >= 21) {
            box.setButtonTintList(sl);
        } else {
            Drawable drawable = ContextCompat.getDrawable(box.getContext(), R.drawable.abc_btn_check_material);
            DrawableWrapper d = new DrawableWrapper(drawable);
            d.setTintList(sl);
            box.setButtonDrawable(d);
        }

    }

    public static int resolveColor(Context context, @AttrRes int attr) {
        return resolveColor(context, attr, 0);
    }

    public static int resolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});

        int var4;
        try {
            var4 = a.getColor(0, fallback);
        } finally {
            a.recycle();
        }

        return var4;
    }

    public static void setBackgroundCompat(View view, Drawable d) {
        if(Build.VERSION.SDK_INT < 16) {
            view.setBackgroundDrawable(d);
        } else {
            view.setBackground(d);
        }

    }

    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round((float) Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static Drawable resolveDrawable(Context context, @AttrRes int attr) {
        return resolveDrawable(context, attr, (Drawable)null);
    }

    private static Drawable resolveDrawable(Context context, @AttrRes int attr, Drawable fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});

        Drawable var5;
        try {
            Drawable d = a.getDrawable(0);
            if(d == null && fallback != null) {
                d = fallback;
            }

            var5 = d;
        } finally {
            a.recycle();
        }

        return var5;
    }

    public static int resolveDimension(Context context, @AttrRes int attr) {
        return resolveDimension(context, attr, -1);
    }

    private static int resolveDimension(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});

        int var4;
        try {
            var4 = a.getDimensionPixelSize(0, fallback);
        } finally {
            a.recycle();
        }

        return var4;
    }

    public static boolean resolveBoolean(Context context, @AttrRes int attr, boolean fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});

        boolean var4;
        try {
            var4 = a.getBoolean(0, fallback);
        } finally {
            a.recycle();
        }

        return var4;
    }

    public static boolean resolveBoolean(Context context, @AttrRes int attr) {
        return resolveBoolean(context, attr, false);
    }
}
