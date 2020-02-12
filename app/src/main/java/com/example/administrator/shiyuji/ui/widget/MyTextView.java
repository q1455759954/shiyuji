package com.example.administrator.shiyuji.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.cache.LruMemoryCache;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.support.sqlit.EmotionsDB;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapUtil;
import com.example.administrator.shiyuji.support.genereter.KeyGenerator;
import com.example.administrator.shiyuji.ui.widget.span.ClickableTextViewMentionLinkOnTouchListener;
import com.example.administrator.shiyuji.ui.widget.span.EmotionSpan;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  自定义textView，
 *   加载表情，添加链接两个功能<br/>
 */

public class MyTextView  extends TextView {
    static final String TAG = "MyTextView";

    public static final LruMemoryCache<String, SpannableString> textSpannableCache = new LruMemoryCache<>(200);

    public static final LruMemoryCache<String, String> textNoneSpannableCache = new LruMemoryCache<>(200);

    public static final LruMemoryCache<String, Bitmap> emotionCache = new LruMemoryCache<>(30);

    private static LinkedBlockingQueue<String> textQueue = new LinkedBlockingQueue<>();

    private static List<MyTextView> textViewList = new ArrayList<>();

    private static InnerThread innerThread;

    private static int lineHeight = 0;

    private static Bitmap normalURLBitmap;

    private static Bitmap videoURLBitmap;

    private static Bitmap photoURLBitmap;

    private String content;

    private boolean innerWeb = false;//改动！！！

    private String textKey;
    private SpannableString textSpannable;

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        textViewList.add(this);

        if (!TextUtils.isEmpty(textKey)) {
            SpannableString spannableString = textSpannableCache.get(textKey);
            if (spannableString != null) {
                setTextSpannable(spannableString);
            }
            else {
                addText(content);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        textViewList.remove(this);
    }

    private void setTextSpannable(SpannableString textSpannable) {
        if (this.textSpannable == textSpannable) {
            return;
        }

        this.textSpannable = textSpannable;

        textViewList.remove(this);


        super.setText(textSpannable);
    }

    public void setContent(String text) {
        if (!TextUtils.isEmpty(text) && text.indexOf("http://t.cn/") != -1) {
            if (text.length() == 19) {
                text = text + " .";
            }
        }

        /**
         * 解决有时已经滑动到界面了，但是list里没有导致html解析不出来
         */
        if (!textViewList.contains(this)){
            onAttachedToWindow();
        }
        boolean replace = false;

//        if (!replace)
//            replace = innerWeb != AppSettings.isInnerBrower();
//
//        innerWeb = AppSettings.isInnerBrower();
        if (false){
            super.setText(text);
            return;
        }


//        if (!replace && TextUtils.isEmpty(text)) {
//           ;
//        }

        // 内容未变化
        if (!replace && !TextUtils.isEmpty(content) && content.equals(text))
            return;

        content = text;
        textKey = KeyGenerator.generateMD5(text);

        SpannableString textSpannable = textSpannableCache.get(textKey);
        if (textSpannable == null) {
            super.setText(text);

            addText(text);
        }
        else {
            setTextSpannable(textSpannable);
        }

        setClickable(false);
        setOnTouchListener(onTouchListener);
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {

        ClickableTextViewMentionLinkOnTouchListener listener = new ClickableTextViewMentionLinkOnTouchListener();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return listener.onTouch(v, event);

        }
    };

        public static void addText(String text) {

        synchronized (textQueue) {
            String key = KeyGenerator.generateMD5(text);

            if (textSpannableCache.get(key) == null) {
                textQueue.add(text);
            }

            if (innerThread == null || !innerThread.isAlive()) {
                innerThread = new InnerThread();
                innerThread.start();
            }
        }
    }

    static Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String key = msg.getData().getString("key");
            if (!TextUtils.isEmpty(key)) {
                List<MyTextView> copyList = new ArrayList<>();
                copyList.addAll(textViewList);
                Iterator<MyTextView> iterator = copyList.iterator();
                while (iterator.hasNext()) {
                    MyTextView textView = iterator.next();

                    SpannableString textSpannable = textSpannableCache.get(key);

                    if (key.equals(textView.textKey) && textSpannable != null) {
                        textView.setTextSpannable(textSpannable);
                    }
                }
            }
        }

    };

    static class InnerThread extends Thread {

        @Override
        public void run() {

            while (true) {
                try {
                    String text = textQueue.poll(60, TimeUnit.SECONDS);

                    if (text == null) {
                        innerThread = null;
                        break;
                    }
                    else {
                        if (GlobalContext.getInstance() == null) {
                            break;
                        }

                        String textKey = KeyGenerator.generateMD5(text);
                        if (textNoneSpannableCache.get(textKey) != null) {
                            continue;
                        }

                        // 获得行高
                        int lineH = 0;
                        while (lineH == 0) {
                            synchronized (textViewList) {
                                if (textViewList.size() == 0)
                                    continue;

                                TextView textView = textViewList.get(0);
                                if (textView != null)
                                    lineH = textView.getLineHeight();
                            }
                        }
                        if (lineHeight != lineH) {
                            emotionCache.evictAll();
                            lineHeight = lineH;
                        }

                        Resources res = GlobalContext.getInstance().getResources();
                        if (normalURLBitmap == null) {
                            normalURLBitmap = BitmapFactory.decodeResource(res, R.drawable.timeline_card_small_web);
                            normalURLBitmap = BitmapUtil.zoomBitmap(normalURLBitmap, Math.round(lineHeight * 4.0f / 5));
                        }
                        if (videoURLBitmap == null) {
                            videoURLBitmap = BitmapFactory.decodeResource(res, R.drawable.timeline_card_small_video);
                            videoURLBitmap = BitmapUtil.zoomBitmap(videoURLBitmap, Math.round(lineHeight * 4.0f / 5));
                        }
                        if (photoURLBitmap == null) {
                            photoURLBitmap = BitmapFactory.decodeResource(res, R.drawable.timeline_icon_photo);
                            photoURLBitmap = BitmapUtil.zoomBitmap(photoURLBitmap, Math.round(lineHeight * 4.0f / 5));
                        }

                        boolean find = false;

                        SpannableString spannableString = SpannableString.valueOf(text);

                        Matcher localMatcher = Pattern.compile("\\[(\\S+?)\\]").matcher(spannableString);
                        while (localMatcher.find()) {
                            String key = localMatcher.group(0);

                            int k = localMatcher.start();
                            int m = localMatcher.end();

                            //在数据库里获取表情
                            byte[] data = EmotionsDB.getEmotion(key);
//                            byte[] data = null;//原来的是上面注释的
                            if (data == null)
                                continue;

                            find = true;

                            synchronized (emotionCache) {
                                Bitmap bitmap = emotionCache.get(key);
                                if (bitmap == null) {
                                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    bitmap = BitmapUtil.zoomBitmap(bitmap, lineHeight-8);

                                    // 添加到内存中
                                    emotionCache.put(key, bitmap);
                                }

                                EmotionSpan l = new EmotionSpan(GlobalContext.getInstance(), bitmap, ImageSpan.ALIGN_BASELINE);
                                spannableString.setSpan(l, k, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                        // 用户名称
                        // Pattern pattern = Pattern.compile("@([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)");
                        Pattern pattern = Pattern.compile("@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}");
                        String scheme = "org.aisen.weibo.sina.userinfo://";
                        Linkify.addLinks(spannableString, pattern, scheme);

                        // 网页链接
                        scheme = "http://";
                        // 启用内置浏览器
                        if (AppSettings.isInnerBrower())
                            scheme = "aisen://";
                        Linkify.addLinks(spannableString, Pattern.compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]"), scheme);

                        // 话题
                        Pattern dd = Pattern.compile("#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#");
                        //Pattern dd = Pattern.compile("#([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)#");
                        scheme = "org.aisen.weibo.sina.topics://";
                        Linkify.addLinks(spannableString, dd, scheme);

                        URLSpan[] urlSpans = spannableString.getSpans(0, spannableString.length(), URLSpan.class);
                        Object weiboSpan = null;
                        for (URLSpan urlSpan : urlSpans) {
                            find = true;

                            int start = spannableString.getSpanStart(urlSpan);
                            int end = spannableString.getSpanEnd(urlSpan);
                            try {
                                spannableString.removeSpan(urlSpan);
                            } catch (Exception e) {
                            }

                            Uri uri = Uri.parse(urlSpan.getURL());
                            String id = KeyGenerator.generateMD5(uri.toString().replace("aisen://", ""));
//                            VideoBean videoBean = SinaDB.getDB().selectById(null, VideoBean.class, id);
//                            if (videoBean != null) {
//                                WebURLEmotionSpan webURLEmotionSpan;
//                                if (VideoService.isVideo(videoBean.getType())) {
//                                    webURLEmotionSpan = new WebURLEmotionSpan(GlobalContext.getInstance(), videoURLBitmap, urlSpan.getURL(), videoBean.getType(), ImageSpan.ALIGN_BASELINE);
//
//                                    Logger.d(TAG, "id[%s], url[%s], video", id, urlSpan.getURL());
//                                }
//                                else if (videoBean.getType() == VideoService.TYPE_PHOTO) {
//                                    webURLEmotionSpan = new WebURLEmotionSpan(GlobalContext.getInstance(), photoURLBitmap, urlSpan.getURL(), videoBean.getType(), ImageSpan.ALIGN_BASELINE);
//
//                                    Logger.d(TAG, "id[%s], url[%s], video", id, urlSpan.getURL());
//                                }
//                                else {
//                                    webURLEmotionSpan = new WebURLEmotionSpan(GlobalContext.getInstance(), normalURLBitmap, urlSpan.getURL(), videoBean.getType(), ImageSpan.ALIGN_BASELINE);
//
//                                    Logger.d(TAG, "id[%s], url[%s], normal", id, urlSpan.getURL());
//                                }
//
//                                spannableString.setSpan(webURLEmotionSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            }
//                            else {
//                                Logger.d(TAG, "id[%s], url[%s], none", id, urlSpan.getURL());
//
//                                weiboSpan = new MyURLSpan(urlSpan.getURL());
//                                spannableString.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            }
                        }
                        String te = spannableString.toString();
                        CharSequence richText = null;

//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                            richText =Html.fromHtml(te,Html.FROM_HTML_MODE_LEGACY);
//                        } else {
//                            richText = Html.fromHtml(te);
//                        }
//                            mTVText.setMovementMethod(LinkMovementMethod.getInstance());

                        //解析图片
                        final Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            public Drawable getDrawable(String source) {
                                if (!source.startsWith("http")){
                                    source="http:"+source;
                                }
                                //在此必须异步加载图片
                                Drawable d = null;
                                try {
                                    Request request = (new Request.Builder()).url(source).build();
                                    Response response = GlobalContext.getOkHttpClient().newCall(request).execute();
                                    InputStream is = response.body().byteStream();
                                    Bitmap bm = BitmapFactory.decodeStream(is);
                                    d = new BitmapDrawable(bm);
                                    //setBounds(0, 0, bm.getWidth(), bm.getHeight());
                                    d.setBounds(0, 0, lineHeight-8, lineHeight-8);
                                } catch (Exception e) {e.printStackTrace();}
                                return d;
                            }
                        };
                        richText = Html.fromHtml(te, imageGetter, null);
                        spannableString = SpannableString.valueOf(richText);
                        if (!richText.toString().equals(text)){
                            find=true;
                        }


                        if (find) {
                            textSpannableCache.put(textKey, spannableString);
                            Message msg = mHandler.obtainMessage();
                            msg.getData().putString("key", textKey);
                            msg.sendToTarget();
                        }
                        else {
                            textNoneSpannableCache.put(textKey, text);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}

