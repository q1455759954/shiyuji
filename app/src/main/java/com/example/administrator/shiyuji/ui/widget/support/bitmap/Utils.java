package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLES10;
import android.provider.MediaStore;
import android.support.annotation.AttrRes;
import android.text.TextUtils;
import android.util.TypedValue;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2019/7/2.
 */

public class Utils {
    public Utils() {
    }

    public static int getBitmapMaxWidthAndMaxHeight() {
        int[] maxSizeArray = new int[1];
        GLES10.glGetIntegerv(3379, maxSizeArray, 0);
        if(maxSizeArray[0] == 0) {
            GLES10.glGetIntegerv(3379, maxSizeArray, 0);
        }

        return 2048;
    }

    public static int getAppHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.height();
    }

    public static Intent getShareIntent(String title, String content, String url) {
        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType("text/plain");
        shareIntent.putExtra("imageURL", url);
        if(!TextUtils.isEmpty(url)) {
            File file = BitmapLoader.getInstance().getCacheFile(url);
            if(file.exists()) {
                shareIntent.setType("image/*");
                Uri uri = Uri.fromFile(file);
                shareIntent.putExtra("android.intent.extra.STREAM", uri);
            }
        }

        if(TextUtils.isEmpty(content)) {
            content = title;
        } else if(!TextUtils.isEmpty(title)) {
            shareIntent.putExtra("android.intent.extra.TITLE", title);
        }

        shareIntent.putExtra("android.intent.extra.TEXT", content);
        return shareIntent;
    }

    public static int dip2px(Context context, float dipValue) {
        float reSize = context.getResources().getDisplayMetrics().density;
        return (int)((double)(dipValue * reSize) + 0.5D);
    }

    public static int px2dip(Context context, int pxValue) {
        float reSize = context.getResources().getDisplayMetrics().density;
        return (int)((double)((float)pxValue / reSize) + 0.5D);
    }

    public static float sp2px(Context context, int spValue) {
        return TypedValue.applyDimension(2, (float)spValue, context.getResources().getDisplayMetrics());
    }

    public static int length(String paramString) {
        int i = 0;

        for(int j = 0; j < paramString.length(); ++j) {
            if(paramString.substring(j, j + 1).matches("[Α-￥]")) {
                i += 2;
            } else {
                ++i;
            }
        }

        if(i % 2 > 0) {
            i = 1 + i / 2;
        } else {
            i /= 2;
        }

        return i;
    }

    public static boolean isIntentSafe(Activity activity, Uri uri) {
        Intent mapCall = new Intent("android.intent.action.VIEW", uri);
        PackageManager packageManager = activity.getPackageManager();
        List activities = packageManager.queryIntentActivities(mapCall, 0);
        return activities.size() > 0;
    }

    public static boolean isIntentSafe(Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, 0);
        return activities.size() > 0;
    }

    public static boolean isGooglePlaySafe(Activity activity) {
        Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms");
        Intent mapCall = new Intent("android.intent.action.VIEW", uri);
        mapCall.addFlags(134217728);
        mapCall.setPackage("com.android.vending");
        PackageManager packageManager = activity.getPackageManager();
        List activities = packageManager.queryIntentActivities(mapCall, 0);
        return activities.size() > 0;
    }

    public static String getLatestCameraPicture(Activity activity) {
        String[] projection = new String[]{"_id", "_data", "bucket_display_name", "datetaken", "mime_type"};
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, (String)null, (String[])null, "datetaken DESC");
        if(cursor.moveToFirst()) {
            String path = cursor.getString(1);
            return path;
        } else {
            return null;
        }
    }

    public static Drawable resolveDrawable(Context context, @AttrRes int attr) {
        return resolveDrawable(context, attr, (Drawable)null);
    }

    public static Drawable resolveDrawable(Context context, @AttrRes int attr, Drawable fallback) {
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
}
