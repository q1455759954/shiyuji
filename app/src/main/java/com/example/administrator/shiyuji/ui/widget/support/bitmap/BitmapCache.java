package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import com.example.administrator.shiyuji.util.cache.LruMemoryCache;
import com.example.administrator.shiyuji.support.genereter.KeyGenerator;
import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;

/**
 * Created by Administrator on 2019/7/2.
 */

public class BitmapCache {
    private LruMemoryCache<String, MyBitmap> mMemoryCache;

    public BitmapCache(int memCacheSize) {
        this.init(memCacheSize);
    }

    private void init(final int memCacheSize) {
        this.mMemoryCache = new LruMemoryCache(memCacheSize) {
            protected int sizeOf(String key, MyBitmap bitmap) {
                return BitmapCommonUtils.getBitmapSize(bitmap.getBitmap()) * 4;
            }
        };
    }

    public void addBitmapToMemCache(String url, ImageConfig config, MyBitmap bitmap) {
        if(url != null && bitmap != null) {
            if(this.mMemoryCache != null) {
                this.mMemoryCache.put(KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(url, config)), bitmap);
            }

        }
    }

    public MyBitmap getBitmapFromMemCache(String url, ImageConfig config) {
        if(this.mMemoryCache != null) {
            MyBitmap memBitmap = (MyBitmap)this.mMemoryCache.get(KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(url, config)));
            if(memBitmap != null) {
                return memBitmap;
            }
        }

        return null;
    }

    public void clearMemCache() {
        if(this.mMemoryCache != null) {
            this.mMemoryCache.evictAll();
        }

    }

    public void clearMemHalfCache() {
        if(this.mMemoryCache != null) {
            this.mMemoryCache.evictHalf();
        }

    }
}
