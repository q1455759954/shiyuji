package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/2.
 */

public class MyBitmap {
    static final String TAG = "MyBitmap";
    static int createdCount = 0;
    private String id;
    private String url;
    private Bitmap bitmap;
    private static Map<String, WeakReference<Bitmap>> cacheMap = new HashMap();

    static Bitmap getCacheBitmap(Context context, int resId) {
        String key = String.valueOf(resId);
        Bitmap bitmap = null;
        if(cacheMap.containsKey(key)) {
            bitmap = (Bitmap)((WeakReference)cacheMap.get(key)).get();
        }

        if(bitmap == null) {
            try {
                bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                cacheMap.put(key, new WeakReference(bitmap));
            } catch (Error var5) {
                return Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_4444);
            }
        }

        return bitmap;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        --createdCount;
//        Logger.v("MyBitmap", createdCount + "");
    }

    public MyBitmap(Context context, int resId) {
        this();
        this.bitmap = getCacheBitmap(context, resId);
    }

    public MyBitmap(Context context, int resId, String url) {
        this();
        this.bitmap = getCacheBitmap(context, resId);
        this.url = url;
    }

    public MyBitmap(Bitmap bitmap, String url) {
        this();
        this.url = url;
        this.bitmap = bitmap;
    }

    private MyBitmap() {
        ++createdCount;
//        Logger.v("MyBitmap", createdCount + "");
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
