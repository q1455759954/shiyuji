package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by Administrator on 2019/7/2.
 */

public class BitmapCommonUtils {
    public BitmapCommonUtils() {
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath = "mounted".equals(Environment.getExternalStorageState())?getExternalCacheDir(context).getPath():context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }

    public static int getBitmapSize(Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static File getExternalCacheDir(Context context) {
        String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static long getUsableSpace(File path) {
        StatFs stats = new StatFs(path.getPath());
        return (long)stats.getBlockSize() * (long)stats.getAvailableBlocks();
    }
}
