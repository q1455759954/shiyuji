package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;

import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.common.utils.SystemUtils;

import java.io.File;

/**
 * 动态和发布中的图片压缩
 * Created by Administrator on 2019/8/4.
 */

public class TimelineBitmapCompress implements IBitmapCompress {

    public static final String TAG = "TimelineCompress";

    @Override
    public MyBitmap compress(byte[] bitmapBytes, File file, String url, ImageConfig config, int origW, int origH) throws Exception {
//        Logger.v("ATimeline", "压缩小图片");

        Bitmap bitmap = null;

        int maxWidth = config.getMaxWidth() == 0 ? SystemUtils.getScreenWidth(GlobalContext.getInstance()) : config.getMaxWidth();
        int maxHeight = config.getMaxHeight() == 0 ? SystemUtils.getScreenHeight(GlobalContext.getInstance()) : config.getMaxHeight();

        // 如果高度比宽度在2倍以上，取高度的一部分
        if (origH * 1.0f / origW > 2) {
            int reqHeight = maxHeight;

            // 截取局部图片
            BitmapRegionDecoder bitmapDecoder = BitmapRegionDecoder.newInstance(bitmapBytes, 0, bitmapBytes.length, true);
            Rect rect = new Rect(0, 0, origW, reqHeight);
            bitmap = bitmapDecoder.decodeRegion(rect, null).copy(Bitmap.Config.ARGB_8888, true);

        } else {
            bitmap = BitmapDecoder.decodeSampledBitmapFromByte(bitmapBytes, maxWidth, maxHeight);
        }

//        Logger.d(TAG, String.format("bitmap width = %d, height = %d", bitmap.getWidth(), bitmap.getHeight()));
        return new MyBitmap(bitmap, url);
    }

}
