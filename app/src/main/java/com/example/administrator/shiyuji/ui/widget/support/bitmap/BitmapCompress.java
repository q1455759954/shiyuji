package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;

import java.io.File;

/**
 * Created by Administrator on 2019/7/2.
 */

public class BitmapCompress implements IBitmapCompress {
    public BitmapCompress() {
    }

    public MyBitmap compress(byte[] bitmapBytes, File file, String url, ImageConfig config, int origW, int origH) throws Exception {
        Bitmap bitmap = null;

        try {
            if(config.getMaxHeight() > 0 && config.getMaxWidth() > 0) {
                bitmap = BitmapDecoder.decodeSampledBitmapFromByte(bitmapBytes, config.getMaxWidth(), config.getMaxHeight());
            } else if(config.getMaxHeight() > 0) {
                bitmap = BitmapDecoder.decodeSampledBitmapFromByte(bitmapBytes, config.getMaxHeight(), config.getMaxHeight());
            } else if(config.getMaxWidth() > 0) {
                bitmap = BitmapDecoder.decodeSampledBitmapFromByte(bitmapBytes, config.getMaxWidth(), config.getMaxWidth());
            } else {
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            }
        } catch (OutOfMemoryError var9) {
            var9.printStackTrace();
        }

//        Logger.d("BitmapLoader", String.format("原始尺寸是%dX%d, 压缩后尺寸是%dX%d", new Object[]{Integer.valueOf(origW), Integer.valueOf(origH), Integer.valueOf(bitmap.getWidth()), Integer.valueOf(bitmap.getHeight())}));
        return new MyBitmap(bitmap, url);
    }
}
