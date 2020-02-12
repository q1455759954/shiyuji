package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.administrator.shiyuji.support.genereter.KeyGenerator;
import com.example.administrator.shiyuji.util.common.utils.SystemUtils;
import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;
import com.example.administrator.shiyuji.ui.widget.support.download.Downloader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2019/7/2.
 */

public class BitmapLoader {
    public static final String TAG = "BitmapLoader";
    private Map<WeakReference<BitmapOwner>, List<WeakReference<MyBitmapLoaderTask>>> ownerMap;
    private Map<String, WeakReference<BitmapLoader.MyBitmapLoaderTask>> taskCache;
    private String imageCachePath;
    private BitmapProcess bitmapProcess;
    private BitmapCache mImageCache;
    private Context mContext;
    private static BitmapLoader imageLoader;

    private BitmapLoader(Context mContext) {
        this.mContext = mContext;
    }

    static BitmapLoader newInstance(Context mContext) {
        imageLoader = new BitmapLoader(mContext);
        return imageLoader;
    }

    public static BitmapLoader newInstance(Context mContext, String imageCachePath) {
        BitmapLoader loader = newInstance(mContext);
        if(TextUtils.isEmpty(imageCachePath)) {
            imageCachePath = SystemUtils.getSdcardPath() + File.separator + "aisenImage" + File.separator;
        }

        loader.imageCachePath = imageCachePath;
        loader.init();
        return loader;
    }

    public static BitmapLoader getInstance() {
        return imageLoader;
    }

    public void destory() {
    }

    BitmapLoader init() {
        this.ownerMap = new HashMap();
        this.taskCache = new HashMap();
        int memCacheSize = 1048576 * ((ActivityManager)this.mContext.getSystemService("activity")).getMemoryClass();
        memCacheSize /= 3;
//        Logger.i("BitmapLoader", "memCacheSize = " + memCacheSize / 1024 / 1024 + "MB");
        this.bitmapProcess = new BitmapProcess(this.imageCachePath);
        this.mImageCache = new BitmapCache(memCacheSize);
        return this;
    }

    public void display(BitmapOwner owner, String url, ImageView imageView, ImageConfig imageConfig) {
        if(TextUtils.isEmpty(url)) {
            this.setImageFaild(imageView, imageConfig);
        } else if(!this.bitmapHasBeenSet(imageView, url)) {
            MyBitmap myBitmap = this.mImageCache.getBitmapFromMemCache(url, imageConfig);
            if(myBitmap != null && imageView != null) {
                imageView.setImageDrawable(new MyDrawable(this.mContext.getResources(), myBitmap, imageConfig, (WeakReference)null));
            } else if(!this.checkTaskExistAndRunning(url, imageView, imageConfig)) {
                boolean canLoad = owner == null || owner.canDisplay();
                if(!canLoad) {
//                    Logger.d("B、itmapLoader", "视图在滚动，显示默认图片");
                    this.setImageLoading(imageView, (String)null, imageConfig);
                } else {
                    BitmapLoader.MyBitmapLoaderTask newTask = new BitmapLoader.MyBitmapLoaderTask(url, imageView, this, imageConfig);
                    WeakReference taskReference = new WeakReference(newTask);
                    this.taskCache.put(KeyGenerator.generateMD5(getKeyByConfig(url, imageConfig)), taskReference);
                    this.setImageLoading(imageView, url, imageConfig);
                    newTask.executrOnImageExecutor(new Void[0]);
                    if(owner != null) {
                        this.getTaskCache(owner).add(new WeakReference(newTask));
                    }

                    newTask = null;
                }
            }

        }
    }

    public boolean bitmapHasBeenSet(ImageView imageView, String url) {
        if(imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if(drawable != null) {
                if(drawable instanceof TransitionDrawable) {
                    TransitionDrawable myDrawable = (TransitionDrawable)drawable;
                    drawable = myDrawable.getDrawable(1);
                }

                if(drawable instanceof MyDrawable) {
                    MyDrawable myDrawable1 = (MyDrawable)drawable;
                    if(myDrawable1.getMyBitmap() != null && url.equals(myDrawable1.getMyBitmap().getUrl())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public BitmapCache getImageCache() {
        return this.mImageCache;
    }

    public BitmapProcess getBitmapProcess() {
        return this.bitmapProcess;
    }

    private List<WeakReference<BitmapLoader.MyBitmapLoaderTask>> getTaskCache(BitmapOwner owner) {
        Object taskWorkInOwner = null;
        Set set = this.ownerMap.keySet();
        Iterator var4 = set.iterator();

        while(var4.hasNext()) {
            WeakReference key = (WeakReference)var4.next();
            if(key != null && key.get() == owner) {
                taskWorkInOwner = (List)this.ownerMap.get(key);
            }
        }

        if(taskWorkInOwner == null) {
            taskWorkInOwner = new ArrayList();
            this.ownerMap.put(new WeakReference(owner), (List<WeakReference<MyBitmapLoaderTask>>) taskWorkInOwner);
        }

        return (List)taskWorkInOwner;
    }

    private boolean checkTaskExistAndRunning(String url, ImageView imageView, ImageConfig config) {
        if(imageView == null) {
            return false;
        } else {
            WeakReference loader = (WeakReference)this.taskCache.get(KeyGenerator.generateMD5(getKeyByConfig(url, config)));
            BitmapLoader.MyBitmapLoaderTask task;
            if(loader != null) {
                task = (BitmapLoader.MyBitmapLoaderTask)loader.get();
                if(task != null) {
                    if(!task.isCancelled() && !task.isCompleted && task.imageUrl.equals(url)) {
                        try {
                            this.setImageLoading(imageView, url, config);
                            task.imageViewsRef.add(new WeakReference(imageView));
//                            Logger.d("BitmapLoader", String.format("ImageView加载的图片已有线程在运行，url = %s", new Object[]{url}));
                        } catch (OutOfMemoryError var7) {
                            var7.printStackTrace();
                        }

                        return true;
                    }
                } else {
                    this.taskCache.remove(KeyGenerator.generateMD5(getKeyByConfig(url, config)));
                }
            }

            task = this.getWorkingTask(imageView);
            if(task != null && !task.imageUrl.equals(url) && task.imageViewsRef.size() == 1) {
//                Logger.d("BitmapLoader", String.format("停止一个图片加载，如果还没有运行 url = %s", new Object[]{url}));
                task.cancel(false);
            }

            return false;
        }
    }

    public void cancelPotentialTask(BitmapOwner owner) {
        if(owner != null) {
            List taskWorkInFragment = this.getTaskCache(owner);
            Iterator var3;
            WeakReference key;
            if(taskWorkInFragment != null) {
                var3 = taskWorkInFragment.iterator();

                while(var3.hasNext()) {
                    key = (WeakReference)var3.next();
                    BitmapLoader.MyBitmapLoaderTask task = (BitmapLoader.MyBitmapLoaderTask)key.get();
                    if(task != null) {
                        task.cancel(true);
//                        Logger.d("BitmapLoader", String.format("fragemnt销毁，停止线程 url = %s", new Object[]{task.imageUrl}));
                    }
                }
            }

            var3 = this.ownerMap.keySet().iterator();

            while(var3.hasNext()) {
                key = (WeakReference)var3.next();
                if(key != null && key.get() == owner) {
                    this.ownerMap.remove(key);
//                    Logger.w("BitmapLoader", "移除一个owner --->" + owner.toString());
                    break;
                }
            }

//            Logger.w("BitmapLoader", "owner %d 个" + this.ownerMap.size());
        }
    }

    private BitmapLoader.MyBitmapLoaderTask getWorkingTask(ImageView imageView) {
        if(imageView == null) {
            return null;
        } else {
            Drawable drawable = imageView.getDrawable();
            if(drawable != null && drawable instanceof MyDrawable) {
                WeakReference loader = ((MyDrawable)drawable).getTask();
                if(loader != null && loader.get() != null) {
                    return (BitmapLoader.MyBitmapLoaderTask)loader.get();
                }
            }

            return null;
        }
    }

    public File getCacheFile(String url) {
        return this.bitmapProcess.getOirgFile(url);
    }

    public File getCompressCacheFile(String url, String imageId) {
        return this.bitmapProcess.getCompressFile(url, imageId);
    }

    public Bitmap getBitmapFromMemory(String url, ImageConfig config) {
        MyBitmap bitmap = this.mImageCache.getBitmapFromMemCache(url, config);
        return bitmap != null?bitmap.getBitmap():null;
    }

    public MyBitmap getMyBitmapFromMemory(String url, ImageConfig config) {
        MyBitmap bitmap = this.mImageCache.getBitmapFromMemCache(url, config);
        return bitmap != null?bitmap:null;
    }

    public static String getKeyByConfig(String url, ImageConfig config) {
        return config != null && !TextUtils.isEmpty(config.getId())?url + config.getId():url;
    }

    public void clearCache() {
        (new BitmapLoader.CacheExecutecTask()).execute(new Object[]{Integer.valueOf(0)});
    }

    public void clearHalfCache() {
        (new BitmapLoader.CacheExecutecTask()).execute(new Object[]{Integer.valueOf(4)});
    }

    /**
     * 根据url和配置加载图片
     * @param imageUrl
     * @param config
     * @return
     * @throws Exception
     */
    public BitmapLoader.BitmapBytesAndFlag doDownload(String imageUrl, ImageConfig config) throws Exception {
        Object bitmapBytes = null;
        int flag = 0;
        //读缓存，找到返回，找不到加载然后缓存下来
        byte[] bitmapBytes1 = this.bitmapProcess.getBitmapFromCompDiskCache(imageUrl, config);
        if(bitmapBytes1 != null) {
//            Logger.v("BitmapLoader", "load the picture through the compress disk, url = " + imageUrl);
            flag |= 1;
        }

        if(bitmapBytes1 == null) {
            bitmapBytes1 = this.bitmapProcess.getBitmapFromOrigDiskCache(imageUrl, config);
            if(bitmapBytes1 != null) {
//                Logger.v("BitmapLoader", "load the data through the original disk, url = " + imageUrl);
                flag |= 2;
            }
        }

        if(bitmapBytes1 == null) {
            bitmapBytes1 = ((Downloader)config.getDownloaderClass().newInstance()).downloadBitmap(this.mContext, imageUrl, config);
            if(bitmapBytes1 != null) {
//                Logger.v("BitmapLoader", "load the data through the network, url = " + imageUrl);
//                Logger.v("BitmapLoader", "downloader = " + config.getDownloaderClass().getSimpleName());
                flag |= 4;
            }

            if(bitmapBytes1 != null && config.isCacheEnable()) {
                this.bitmapProcess.writeBytesToOrigDisk(bitmapBytes1, imageUrl);
            }
        }

        if(bitmapBytes1 != null && config.getProgress() != null) {
            config.getProgress().sendFinishedDownload(bitmapBytes1);
        }

        if(bitmapBytes1 != null) {
            BitmapLoader.BitmapBytesAndFlag bitmapBytesAndFlag = new BitmapLoader.BitmapBytesAndFlag();
            bitmapBytesAndFlag.bitmapBytes = bitmapBytes1;
            bitmapBytesAndFlag.flag = flag;
            return bitmapBytesAndFlag;
        } else {
            throw new Exception("download faild : " + imageUrl);
        }
    }

    private void clearMemCacheInternal() {
//        Logger.d("BitmapLoader", "clearMemCacheInternal");
        if(this.mImageCache != null) {
            this.mImageCache.clearMemCache();
        }

    }

    public void clearMemHalfCacheInternal() {
        if(this.mImageCache != null) {
            this.mImageCache.clearMemHalfCache();
        }

    }

    public static Drawable getLoadingDrawable(Context context, ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if(drawable != null && context != null) {
            if(drawable instanceof TransitionDrawable) {
                TransitionDrawable myDrawable = (TransitionDrawable)drawable;
                drawable = myDrawable.getDrawable(1);
            }

            if(drawable instanceof MyDrawable) {
                MyDrawable myDrawable1 = (MyDrawable)drawable;
                ImageConfig config = myDrawable1.getConfig();
                if(config != null && config.getLoadingRes() > 0) {
                    return new BitmapDrawable(context.getResources(), (new MyBitmap(context, config.getLoadingRes())).getBitmap());
                }
            }
        }

        return new ColorDrawable(Color.parseColor("#fff2f2f2"));
    }

    private void setImageFaild(ImageView imageView, ImageConfig imageConfig) {
        if(imageView != null && imageConfig.getLoadfaildRes() > 0) {
            imageView.setImageDrawable(new MyDrawable(this.mContext.getResources(), new MyBitmap(this.mContext, imageConfig.getLoadfaildRes()), imageConfig, (WeakReference)null));
        }

    }

    private void setImageLoading(ImageView imageView, String url, ImageConfig imageConfig) {
        if(imageView != null && imageConfig.getLoadingRes() > 0) {
            imageView.setImageDrawable(new MyDrawable(this.mContext.getResources(), new MyBitmap(this.mContext, imageConfig.getLoadingRes(), url), imageConfig, (WeakReference)null));
        }

    }

    private class CacheExecutecTask extends AsyncTask<Object, Void, Void> {
        public static final int MESSAGE_CLEAR = 0;
        public static final int MESSAGE_HALF_CLEAR = 4;

        private CacheExecutecTask() {
        }

        protected Void doInBackground(Object... params) {
            switch(((Integer)params[0]).intValue()) {
                case 0:
                    BitmapLoader.this.clearMemCacheInternal();
                    break;
                case 4:
                    BitmapLoader.this.clearMemHalfCacheInternal();
            }

            return null;
        }
    }

    public static class BitmapBytesAndFlag {
        public byte[] bitmapBytes;
        public int flag;

        public BitmapBytesAndFlag() {
        }
    }

    public class MyBitmapLoaderTask extends BitmapTask<Void, Void, MyBitmap> {
        private final String imageUrl;
        private List<WeakReference<ImageView>> imageViewsRef;
        private final ImageConfig config;
        boolean isCompleted = false;

        public String getKey() {
            return KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(this.imageUrl, this.config));
        }

        public MyBitmapLoaderTask(String imageUrl, ImageView imageView, BitmapLoader bitmapLoader, ImageConfig config) {
            this.imageUrl = imageUrl;
            this.imageViewsRef = new ArrayList();
            if(imageView != null) {
                this.imageViewsRef.add(new WeakReference(imageView));
            }

            this.config = config;
        }

        public MyBitmap workInBackground(Void... params) throws Exception {
            try {
                BitmapLoader.BitmapBytesAndFlag e = BitmapLoader.this.doDownload(this.imageUrl, this.config);
                byte[] bitmapBytes = e.bitmapBytes;
                int flag = e.flag;
                if(!this.isCancelled() && this.checkImageBinding()) {
                    MyBitmap bitmap = BitmapLoader.this.bitmapProcess.compressBitmap(BitmapLoader.this.mContext, bitmapBytes, this.imageUrl, flag, this.config);
                    if(bitmap != null && bitmap.getBitmap() != null) {
                        BitmapLoader.this.mImageCache.addBitmapToMemCache(this.imageUrl, this.config, bitmap);
                        return bitmap;
                    }

                    BitmapLoader.this.bitmapProcess.deleteFile(this.imageUrl, this.config);
                }
            } catch (OutOfMemoryError var6) {
                var6.printStackTrace();
            }

            throw new Exception("task canceled or failed, bitmap is null, url = " + this.imageUrl);
        }

        protected void onTaskSuccess(MyBitmap bitmap) {
            super.onTaskSuccess(bitmap);
            this.setImageBitmap(bitmap);
        }

        protected void onTaskFailed(Exception exception) {
            super.onTaskFailed(exception);
            if(this.config.getLoadfaildRes() > 0) {
                this.setImageBitmap(new MyBitmap(BitmapLoader.this.mContext, this.config.getLoadfaildRes()));
            }

        }

        private boolean checkImageBinding() {
            for(int i = 0; i < this.imageViewsRef.size(); ++i) {
                ImageView imageView = (ImageView)((WeakReference)this.imageViewsRef.get(i)).get();
                if(imageView != null) {
                    Drawable drawable = imageView.getDrawable();
                    if(drawable != null && drawable instanceof MyDrawable) {
                        MyDrawable aisenDrawable = (MyDrawable)drawable;
                        if(this.imageUrl.equals(aisenDrawable.getMyBitmap().getUrl())) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        void setImageBitmap(MyBitmap bitmap) {
            for(int i = 0; i < this.imageViewsRef.size(); ++i) {
                ImageView imageView = (ImageView)((WeakReference)this.imageViewsRef.get(i)).get();
                if(imageView != null) {
                    Drawable drawable = imageView.getDrawable();
                    if(drawable != null && drawable instanceof MyDrawable) {
                        MyDrawable aisenDrawable = (MyDrawable)drawable;
                        if(this.imageUrl.equals(aisenDrawable.getMyBitmap().getUrl())) {
                            MyDrawable myDrawable = new MyDrawable(BitmapLoader.this.mContext.getResources(), bitmap, this.config, (WeakReference)null);
                            this.config.getDisplayer().loadCompletedisplay(imageView, myDrawable);
                        }
                    }
                }
            }

        }

        protected void onTaskComplete() {
            super.onTaskComplete();
            this.isCompleted = true;
            BitmapLoader.this.taskCache.remove(KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(this.imageUrl, this.config)));
        }
    }
}
