package com.example.administrator.shiyuji.util.viewutil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2019/6/28.
 */

/**
 * 弹窗显示啥的
 */
public class ViewUtils {
    public static ProgressDialog progressDialog2;

    public ViewUtils() {
    }

    public static int getResId(Context context, String resName, String defType) {
        try {
            String packageName = context.getPackageName();
            Resources resources = context.getPackageManager().getResourcesForApplication(packageName);
            int resId = resources.getIdentifier(resName, defType, packageName);
            return resId;
        } catch (Exception var6) {
            return 0;
        }
    }

    public static int getStringResId(Context context, String resName) {
        return getResId(context, resName, "string");
    }

    public static int getDrawableResId(Context context, String resName) {
        return getResId(context, resName, "drawable");
    }

    public static void setTextViewValue(Activity context, int txtId, String content) {
        if(context != null && context.findViewById(txtId) != null) {
            ((TextView)context.findViewById(txtId)).setText(content);
        }

    }

    public static void setTextViewValue(View container, int txtId, String content) {
        if(container != null && container.findViewById(txtId) != null) {
            ((TextView)container.findViewById(txtId)).setText(content);
        }

    }

    public static void setTextViewValue(Activity context, View container, int txtId, int contentId) {
        if(context != null) {
            ((TextView)container.findViewById(txtId)).setText(context.getString(contentId));
        }

    }

    public static void setImgResource(Activity context, int imgId, int sourceId) {
        if(context != null) {
            ((ImageView)context.findViewById(imgId)).setImageResource(sourceId);
        }

    }

    public static void setImgResource(Activity context, int imgId, Bitmap source) {
        if(context != null) {
            ((ImageView)context.findViewById(imgId)).setImageBitmap(source);
        }

    }

    public static void setImgResource(View container, int imgId, int sourceId) {
        ((ImageView)container.findViewById(imgId)).setImageResource(sourceId);
    }

    public static void setImgResource(View container, int imgId, Bitmap source) {
        ((ImageView)container.findViewById(imgId)).setImageBitmap(source);
    }

    public static void showMessage(Context context, String message) {
        if(context != null) {
            Toast.makeText(context, message, 0).show();
        }

    }

    public static void showMessage(Context context, int messageId) {
        if(context != null) {
            Toast.makeText(context, messageId, 0).show();
        }

    }

    public static ProgressDialog createProgressDialog(Activity context, String message, int widgetColor) {
        dismissProgressDialog();
        progressDialog2 = new ViewUtils.MProgressDialog(context, widgetColor);
        progressDialog2.setMessage(message);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setCancelable(false);
        return progressDialog2;
    }

    public static void updateProgressDialog(String message) {
        if(progressDialog2 != null && progressDialog2.isShowing()) {
            progressDialog2.setMessage(message);
        }

    }

    public static void dismissProgressDialog() {
        if(progressDialog2 != null && progressDialog2.isShowing()) {
            try {
                progressDialog2.dismiss();
            } catch (IllegalArgumentException var1) {
                ;
            }

            progressDialog2 = null;
        }

    }

    public static class MProgressDialog extends ProgressDialog {
        private int color;

        public MProgressDialog(Context context, int color) {
            super(context);
            this.color = color;
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(this.color != 0) {
                try {
                    Field progressBarField = ViewUtils.MProgressDialog.class.getSuperclass().getDeclaredField("mProgress");
                    progressBarField.setAccessible(true);
                    ProgressBar progressBar = (ProgressBar)progressBarField.get(this);
                    if(Build.VERSION.SDK_INT >= 21) {
                        ColorStateList mode = ColorStateList.valueOf(this.color);
                        progressBar.setProgressTintList(mode);
                        progressBar.setSecondaryProgressTintList(mode);
                        progressBar.setIndeterminateTintList(mode);
                    } else {
                        PorterDuff.Mode mode1 = PorterDuff.Mode.SRC_IN;
                        if(Build.VERSION.SDK_INT <= 10) {
                            mode1 = PorterDuff.Mode.MULTIPLY;
                        }

                        if(progressBar.getIndeterminateDrawable() != null) {
                            progressBar.getIndeterminateDrawable().setColorFilter(this.color, mode1);
                        }

                        if(progressBar.getProgressDrawable() != null) {
                            progressBar.getProgressDrawable().setColorFilter(this.color, mode1);
                        }
                    }
                } catch (Throwable var5) {
                    ;
                }
            }

        }
    }
}
