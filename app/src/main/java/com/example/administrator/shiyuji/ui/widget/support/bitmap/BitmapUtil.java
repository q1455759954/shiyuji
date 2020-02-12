package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2019/7/1.
 */


public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();

    public BitmapUtil() {
    }

    public static BitmapUtil.BitmapType getType(byte[] imageData) {
        if(imageData.length >= 2 && (imageData[0] & 255) == 255 && (imageData[1] & 216) == 216) {
            return BitmapUtil.BitmapType.jpg;
        } else {
            short[] png;
            if(imageData.length >= 8) {
                png = new short[]{137, 80, 78, 71, 13, 10, 26, 10};

                for(int fourIndex = 0; fourIndex < png.length && (imageData[fourIndex] & png[fourIndex]) == png[fourIndex]; ++fourIndex) {
                    if(fourIndex == png.length - 1) {
                        return BitmapUtil.BitmapType.png;
                    }
                }
            }

            if(imageData.length >= 5) {
                png = new short[]{71, 73, 70, 56, 57, 97};
                byte var4 = 55;

                for(int i = 0; i < png.length && (imageData[i] & png[i]) == png[i] && (i != 4 || (imageData[i] & var4) != var4); ++i) {
                    if(i == png.length - 1) {
                        return BitmapUtil.BitmapType.gif;
                    }
                }
            }

            return imageData.length >= 2 && (imageData[0] & 66) == 66 && (imageData[1] & 77) == 77?BitmapUtil.BitmapType.bmp:BitmapUtil.BitmapType.jpg;
        }
    }

//    public static String getSuffix(BitmapUtil.BitmapType type) {
//        switch(null.$SwitchMap$org$aisen$android$common$utils$BitmapUtil$BitmapType[type.ordinal()]) {
//            case 1:
//                return ".gif";
//            case 2:
//                return ".jpg";
//            case 3:
//                return ".bmp";
//            case 4:
//                return ".png";
//            default:
//                return ".jpg";
//        }
//    }

    public static Bitmap setImageCorner(Bitmap source, float roundPx) {
        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
//        paint.setColor(-65536);
        paint.setXfermode((Xfermode)null);
        paint.setAlpha(255);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0.0F, 0.0F, paint);
        source.recycle();
        return result;
    }

    /**
     * 缩放图片
     * @param source
     * @param width
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap source, int width) {
        Matrix matrix = new Matrix();
        float scale = (float)width * 1.0F / (float)source.getWidth();
        matrix.setScale(scale, scale);
        Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        try {
//            Logger.d(TAG, String.format("zoom bitmap, source(%d,%d) result(%d,%d)", new Object[]{Integer.valueOf(source.getWidth()), Integer.valueOf(source.getHeight()), Integer.valueOf(result.getWidth()), Integer.valueOf(result.getHeight())}));
        } catch (Exception var6) {
            ;
        }

        return result;
    }

    public static Bitmap decodeRegion(byte[] bytes, int width, int height) {
        try {
            BitmapRegionDecoder bitmapDecoder = BitmapRegionDecoder.newInstance(bytes, 0, bytes.length, true);
            Rect rect = new Rect(0, 0, width, height);
            return bitmapDecoder.decodeRegion(rect, (BitmapFactory.Options)null).copy(Bitmap.Config.ARGB_8888, true);
        } catch (Exception var5) {
            return null;
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, int degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate((float)degrees, (float)(source.getWidth() / 2), (float)(source.getHeight() / 2));
        Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        try {
//            Logger.d(TAG, String.format("rotate bitmap, source(%d,%d) result(%d,%d)", new Object[]{Integer.valueOf(source.getWidth()), Integer.valueOf(source.getHeight()), Integer.valueOf(result.getWidth()), Integer.valueOf(result.getHeight())}));
        } catch (Exception var5) {
            ;
        }

        source.recycle();
        return result;
    }

    public static Bitmap getFromDrawableAsBitmap(Context context, String resName) {
        try {
            String e = context.getPackageName();
            Resources resources = context.getPackageManager().getResourcesForApplication(e);
            int resId = resources.getIdentifier(resName, "drawable", e);

            try {
                if(resId != 0) {
                    return BitmapFactory.decodeResource(context.getResources(), resId);
                }

//                Logger.e(String.format("配置的图片ResourceId=%s不存在", new Object[]{resName}));
            } catch (OutOfMemoryError var6) {
                var6.printStackTrace();
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return null;
    }

    public static InputStream getFromDrawableAsStream(Context context, String resName) {
        try {
            String e = context.getPackageName();
            Resources resources = context.getPackageManager().getResourcesForApplication(e);
            int resId = resources.getIdentifier(resName, "drawable", e);
            if(resId != 0) {
                return Bitmap2InputStream(BitmapFactory.decodeResource(resources, resId));
            }

//            Logger.e(String.format("配置的图片ResourceId=%s不存在", new Object[]{resName}));
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return null;
    }

    public static InputStream Byte2InputStream(byte[] b) {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        return bais;
    }

    public static byte[] InputStream2Bytes(InputStream is) {
        String str = "";
        byte[] readByte = new byte[1024];
        boolean readCount = true;

        try {
            while(is.read(readByte, 0, 1024) != -1) {
                str = str + (new String(readByte)).trim();
            }

            return str.getBytes();
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public static Bitmap InputStream2Bitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    public static InputStream Drawable2InputStream(Drawable d) {
        Bitmap bitmap = drawable2Bitmap(d);
        return Bitmap2InputStream(bitmap);
    }

    public static Drawable InputStream2Drawable(Context context, InputStream is) {
        Bitmap bitmap = InputStream2Bitmap(is);
        return bitmap2Drawable(context, bitmap);
    }

    public static byte[] Drawable2Bytes(Drawable d) {
        Bitmap bitmap = drawable2Bitmap(d);
        return Bitmap2Bytes(bitmap);
    }

    public static Drawable Bytes2Drawable(Context context, byte[] b) {
        Bitmap bitmap = Bytes2Bitmap(b);
        return bitmap2Drawable(context, bitmap);
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bitmap(byte[] b) {
        return b.length != 0?BitmapFactory.decodeByteArray(b, 0, b.length):null;
    }

    //这里有问题
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE? Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmap2Drawable(Context context, Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(context.getResources(), bitmap);
        return bd;
    }

    public static enum BitmapType {
        gif,
        jpg,
        png,
        bmp;

        private BitmapType() {
        }
    }
}

