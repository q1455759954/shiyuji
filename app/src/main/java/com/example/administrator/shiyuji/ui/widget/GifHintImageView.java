package com.example.administrator.shiyuji.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapUtil;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.Utils;

/**
 * Created by Administrator on 2019/7/2.
 */


public class GifHintImageView extends ImageView {

    private boolean showGif = false;

    private boolean cut = false;

    private boolean midExist = false;

    private String url;

    public static Bitmap gif = null;
    public static Bitmap cutBitmap = null;

    private int gap = 0;

    private Paint paint;
    private Paint dotPaint;
    private int theme = -1;

    public GifHintImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GifHintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GifHintImageView(Context context) {
        super(context);
    }

    public void setHint(String url) {
        showGif = !TextUtils.isEmpty(url) && url.toLowerCase().endsWith(".gif");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() > 0 && getHeight() > 0) {
            if (paint == null) {
                paint = new Paint();
            }

            if (showGif) {
                if (gif == null) {
                    gif = BitmapFactory.decodeResource(getResources(), R.drawable.ic_gif);
                }

                canvas.drawBitmap(gif, getWidth() - gif.getWidth(), getHeight() - gif.getHeight(), paint);
            }
            else if (cut){
                if (cutBitmap == null) {
                    cutBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cut);
                    cutBitmap = BitmapUtil.zoomBitmap(cutBitmap, Utils.dip2px(getContext(), 30));
                }

                canvas.drawBitmap(cutBitmap, getWidth() - cutBitmap.getWidth(), getHeight() - cutBitmap.getHeight(), paint);
            }

            // 如果高清图已下载，显示提示
//            if (AppSettings.midPicHint() && midExist) {
//                if (dotPaint == null) {
//                    dotPaint = new Paint();
//                    gap = Utils.dip2px(getContext(), 8);
//                }
//                if (theme != AppSettings.getThemeColor()) {
//                    dotPaint.setColor(GlobalContext.getInstance().getResources().getColor(ThemeUtils.themeColorArr[AppSettings.getThemeColor()][0]));
//
//                    theme = AppSettings.getThemeColor();
//                }
//                canvas.drawRect(getWidth() - gap, 0, getWidth(), gap, dotPaint);
//            }
        }

    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        if (getParent() instanceof TimelinePicsView) {
//            MyDrawable myDrawable = null;
//            if (drawable instanceof MyDrawable) {
//                myDrawable = (MyDrawable) drawable;
//            }
//            else if (drawable instanceof TransitionDrawable) {
//                myDrawable = (MyDrawable) ((TransitionDrawable) drawable).getDrawable(1);
//            }
//
//            if (myDrawable != null && TextUtils.isEmpty(myDrawable.getMyBitmap().getId()))
            ((TimelinePicsView) getParent()).checkPicSize();
        }
    }

    public void setMidHint(String url, boolean large) {
        this.url = url;

        String midUrl = url.replace("thumbnail", "bmiddle");
        midExist = BitmapLoader.getInstance().getCacheFile(midUrl).exists() && !large;
    }

    public void setCut(boolean cut) {
        this.cut = cut;
    }

}
