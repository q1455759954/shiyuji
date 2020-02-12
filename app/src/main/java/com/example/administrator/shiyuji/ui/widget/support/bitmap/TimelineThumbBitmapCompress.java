package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.graphics.Bitmap;

import com.example.administrator.shiyuji.ui.widget.TimelinePicsView;
import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;

import java.io.File;

/**
 * Created by Administrator on 2019/7/2.
 */

public class TimelineThumbBitmapCompress extends BitmapCompress {

    public static final int maxHeight = 1000;
    public static final int cutWidth = 550;
    public static final int cutHeight = 900;

    @Override
    public MyBitmap compress(byte[] bitmapBytes, File file, String url, ImageConfig config, int origW, int origH) throws Exception {
        boolean isGif = url.toLowerCase().endsWith("gif");

        if (config instanceof TimelinePicsView.TimelineImageConfig) {
            TimelinePicsView.TimelineImageConfig timelineImageConfig = (TimelinePicsView.TimelineImageConfig) config;

            if (timelineImageConfig.getSize() > 1) {
                if (isGif) {
                }
                else {
                    float maxRadio = 6 * 1.0f / 16;
                    // 图片的宽高比过小，不截gif图
                    if (origW * 1.0f / origH < maxRadio) {
//                        Logger.v(String.format("原始尺寸, width = %d, height = %d", origW, origH));

                        // 根据比例截取图片
                        int width = origW;
                        int height = width * (timelineImageConfig.getShowHeight() / timelineImageConfig.getShowWidth());

                        Bitmap bitmap = BitmapUtil.decodeRegion(bitmapBytes, width, height);
//                        if (bitmap != null)
//                            Logger.v(String.format("截取后的尺寸, width = %d, height = %d", bitmap.getWidth(), bitmap.getHeight()));
                        return new MyBitmap(bitmap, url);
                    }
                }
            }

        }

        // 高度比较高时，截图部分显示
        if (!isGif && origW <= 440 && origH > maxHeight) {
            float outHeight = origW * 1.0f * (cutHeight * 1.0f / cutWidth);
            return new MyBitmap(BitmapUtil.decodeRegion(bitmapBytes, origW, Math.round(outHeight)), url);
        }

        return super.compress(bitmapBytes, file, url, config, origW, origH);
    }

}

