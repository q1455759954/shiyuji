package com.example.administrator.shiyuji.util.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.administrator.shiyuji.ui.activity.base.ActivityHelper;
import com.example.administrator.shiyuji.util.common.GlobalContext;

import java.io.File;

/**
 * Created by Administrator on 2019/7/2.
 */

public class SystemUtils {
    private static int screenWidth;
    private static int screenHeight;
    private static float density;

    /**
     * 发布时带有图片发送广播
     * @param context
     * @param file
     */
    public static void scanPhoto(Context context, File file) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
    /**
     * 得到发布微博应锁住组件高度
     * @param paramActivity
     * @return
     */
    public static int getAppContentHeight(Activity paramActivity) {
        return getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity) - getActionBarHeight(paramActivity) - getKeyboardHeight(paramActivity);
    }

    /**
     *标题栏高度
     * @param context
     * @return
     */
    @TargetApi(14)
    public static int getActionBarHeight(Context context) {
        int result = 0;
        if(Build.VERSION.SDK_INT >= 14) {
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(16843499, tv, true);
            result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }

        return result;
    }
    /**
     * 判断键盘有没有显示
     * @param paramActivity
     * @return
     */
    public static boolean isKeyBoardShow(Activity paramActivity) {
        int height = getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity) - getAppHeight(paramActivity);
        return height != 0;
    }

    /**
     * 显示键盘
     * @param context
     * @param paramEditText
     */
    public static void showKeyBoard(final Context context, final View paramEditText) {
        paramEditText.requestFocus();
        paramEditText.post(new Runnable() {
            public void run() {
                ((InputMethodManager)context.getSystemService("input_method")).showSoftInput(paramEditText, 0);
            }
        });
    }

    /**
     * 隐藏键盘
     * @param context
     * @param paramEditText
     */
    public static void hideSoftInput(Context context, View paramEditText) {
        ((InputMethodManager)context.getSystemService("input_method")).hideSoftInputFromWindow(paramEditText.getWindowToken(), 0);
    }

    /**
     * 得到键盘的高度
     * @param paramActivity
     * @return
     */
    public static int getKeyboardHeight(Activity paramActivity) {
        int height = getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity) - getAppHeight(paramActivity);
        if(height == 0) {
            height = ActivityHelper.getIntShareData(GlobalContext.getInstance(), "KeyboardHeight", 400);
        } else {
            ActivityHelper.putIntShareData(GlobalContext.getInstance(), "KeyboardHeight", height);
        }

        return height;
    }

    public static int getAppHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.height();
    }

    private static void setScreenInfo(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)context.getSystemService("window");
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        density = dm.density;
    }

    public static int getScreenWidth(Context context) {
        if(screenWidth == 0) {
            setScreenInfo(context);
        }

        return screenWidth;
    }

    public static int getScreenHeight(Context context) {
        if(screenHeight == 0) {
            setScreenInfo(context);
        }

        return screenHeight;
    }

    public static boolean hasSDCard() {
        boolean mHasSDcard = false;
        if("mounted".endsWith(Environment.getExternalStorageState())) {
            mHasSDcard = true;
        } else {
            mHasSDcard = false;
        }

        return mHasSDcard;
    }

    public static String getSdcardPath() {
        return hasSDCard()?Environment.getExternalStorageDirectory().getAbsolutePath():"/sdcard/";
    }

    /**
     * KitkatViewGroup里调用,picActivity也有调用
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        return getInternalDimensionSize(context.getResources(), "status_bar_height");
    }
    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if(resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }

        return result;
    }
}
