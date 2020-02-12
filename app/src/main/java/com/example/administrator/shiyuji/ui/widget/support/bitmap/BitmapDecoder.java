package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.administrator.shiyuji.util.common.utils.SystemUtils;

import java.io.FileDescriptor;

/**
 * Created by Administrator on 2019/7/2.
 */

public class BitmapDecoder {
    private static final Bitmap.Config config;

    private BitmapDecoder() {
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromByte(Context context, byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int screenWidth = SystemUtils.getScreenWidth(context);
        int screenHeight = SystemUtils.getScreenHeight(context);
        float reqWidth = (float)screenWidth;
        float reqHeight = (float)screenHeight;
        if(reqWidth > (float)options.outWidth) {
            reqHeight = reqWidth * 1.0F / (float)options.outWidth * (float)options.outHeight;
        }

        if(reqHeight > 3000.0F) {
            reqHeight = 3000.0F;
        }

        return decodeSampledBitmapFromByte(data, (int)reqWidth, (int)reqHeight);
    }

    public static Bitmap decodeSampledBitmapFromByte(byte[] data, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;
        return BitmapFactory.decodeFile(filename, options);
    }

    public static Bitmap decodeSampledBitmapFromDescriptor(Context context, FileDescriptor fileDescriptor) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, (Rect)null, options);
        int screenWidth = SystemUtils.getScreenWidth(context);
        int screenHeight = SystemUtils.getScreenHeight(context);
        float reqWidth = (float)screenWidth * 1.5F;
        float reqHeight = (float)screenHeight;
        if(reqWidth > (float)options.outWidth) {
            reqHeight = reqWidth * 1.0F / (float)options.outWidth * (float)options.outHeight;
        }

        if(reqHeight > 3000.0F) {
            reqHeight = 3000.0F;
        }

        return decodeSampledBitmapFromDescriptor(fileDescriptor, (int)reqWidth, (int)reqHeight);
    }

    public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, (Rect)null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, (Rect)null, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if(height > reqHeight || width > reqWidth) {
            if(width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }

            float totalPixels = (float)(width * height);

            for(float totalReqPixelsCap = (float)(reqWidth * reqHeight * 2); totalPixels / (float)(inSampleSize * inSampleSize) > totalReqPixelsCap; ++inSampleSize) {
                ;
            }
        }

        return inSampleSize;
    }

    static {
        config = Bitmap.Config.ARGB_8888;
    }
}
