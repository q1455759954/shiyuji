package com.example.administrator.shiyuji.ui.widget.support;

import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapCompress;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.IBitmapCompress;
import com.example.administrator.shiyuji.ui.widget.support.download.DownloadProcess;
import com.example.administrator.shiyuji.ui.widget.support.download.Downloader;
import com.example.administrator.shiyuji.ui.widget.support.download.WebDownloader;

/**
 * Created by Administrator on 2019/7/2.
 */

public class ImageConfig {
    private String id;
    private int maxWidth = 0;
    private int maxHeight = 0;
    private int corner;
    private DownloadProcess progress;
    private Class<? extends Downloader> downloaderClass = WebDownloader.class;
    private Class<? extends IBitmapCompress> bitmapCompress = BitmapCompress.class;
    private Displayer displayer = new FadeInDisplayer();
    private int loadingRes;
    private int loadfaildRes;
    private boolean cacheEnable = true;
    private boolean compressCacheEnable = true;

    public ImageConfig() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DownloadProcess getProgress() {
        return this.progress;
    }

    public void setProgress(DownloadProcess progress) {
        this.progress = progress;
    }

    public Class<? extends Downloader> getDownloaderClass() {
        return this.downloaderClass;
    }

    public void setDownloaderClass(Class<? extends Downloader> downloaderClass) {
        this.downloaderClass = downloaderClass;
    }

    public Displayer getDisplayer() {
        return this.displayer;
    }

    public void setDisplayer(Displayer displayer) {
        this.displayer = displayer;
    }

    public Class<? extends IBitmapCompress> getBitmapCompress() {
        return this.bitmapCompress;
    }

    public void setBitmapCompress(Class<? extends IBitmapCompress> bitmapCompress) {
        this.bitmapCompress = bitmapCompress;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getLoadingRes() {
        return this.loadingRes;
    }

    public void setLoadingRes(int loadingRes) {
        this.loadingRes = loadingRes;
    }

    public int getLoadfaildRes() {
        return this.loadfaildRes;
    }

    public void setLoadfaildRes(int loadfaildRes) {
        this.loadfaildRes = loadfaildRes;
    }

    public int getCorner() {
        return this.corner;
    }

    public void setCorner(int corner) {
        this.corner = corner;
    }

    public boolean isCacheEnable() {
        return this.cacheEnable;
    }

    public void setCacheEnable(boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
        this.compressCacheEnable = cacheEnable;
    }

    public boolean isCompressCacheEnable() {
        return this.compressCacheEnable;
    }

    public void setCompressCacheEnable(boolean compressCacheEnable) {
        this.compressCacheEnable = compressCacheEnable;
    }
}
