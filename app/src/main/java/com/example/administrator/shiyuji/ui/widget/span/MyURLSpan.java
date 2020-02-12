package com.example.administrator.shiyuji.ui.widget.span;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;

/**
 * Created by Administrator on 2019/7/1.
 */


public class MyURLSpan extends ClickableSpan {
    private final String mURL;
    private int color;

    public MyURLSpan(String url) {
        this.mURL = url;
    }

    public MyURLSpan(Parcel src) {
        this.mURL = src.readString();
    }

    public int getSpanTypeId() {
        return 11;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mURL);
    }

    public String getURL() {
        return this.mURL;
    }

    public void onClick(View widget) {
//        Logger.v(MyURLSpan.class.getSimpleName(), String.format("the link(%s) was clicked ", new Object[]{this.getURL()}));
        Uri uri = Uri.parse(this.getURL());
        Context context = widget.getContext();
        Intent intent;
        if(uri.getScheme().startsWith("http")) {
            intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(uri);
            intent.setFlags(268435456);
            context.startActivity(intent);
        } else {
            intent = new Intent("android.intent.action.VIEW", uri);
            intent.putExtra("com.android.browser.application_id", context.getPackageName());
            context.startActivity(intent);
        }

    }

    public void onLongClick(View widget) {
        Uri data = Uri.parse(this.getURL());
        if(data != null) {
            String d = data.toString();
            String newValue = "";
            if(d.startsWith("org.aisen.android.ui")) {
                int cm = d.lastIndexOf("/");
                newValue = d.substring(cm + 1);
            } else if(d.startsWith("http")) {
                newValue = d;
            }

            if(!TextUtils.isEmpty(newValue)) {
                ClipboardManager cm1 = (ClipboardManager)widget.getContext().getSystemService("clipboard");
                cm1.setPrimaryClip(ClipData.newPlainText("ui", newValue));
//                MToast.showMessage(widget.getContext(), String.format(widget.getContext().getString(string.comm_hint_copied), new Object[]{newValue}));
            }
        }

    }

//    public void updateDrawState(TextPaint tp) {
//        if(this.color == 0) {
//            int[] attrs = new int[]{R.attr.colorPrimary};
//            BaseActivity activity = BaseActivity.getRunningActivity();
//            if(activity != null) {
//                TypedArray ta = activity.obtainStyledAttributes(attrs);
//                tp.setColor(ta.getColor(0, -16776961));
//            }
//        } else {
//            tp.setColor(this.color);
//        }
//
//    }

    public void setColor(int color) {
        this.color = color;
    }
}
