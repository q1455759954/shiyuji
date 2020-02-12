package com.example.administrator.shiyuji.ui.widget.support.download;

import android.content.Context;
import android.net.Uri;

import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;
import com.example.administrator.shiyuji.util.common.utils.FileUtils;

import java.io.InputStream;

/**
 * Created by Administrator on 2019/8/4.
 */


public class ContentProviderDownloader implements Downloader {
    public ContentProviderDownloader() {
    }

    public byte[] downloadBitmap(Context context, String url, ImageConfig config) throws Exception {
        try {
            InputStream e = context.getContentResolver().openInputStream(Uri.parse(url));
            byte[] datas = FileUtils.readStreamToBytes(e);
            return datas;
        } catch (Exception var6) {
            if(config.getProgress() != null) {
                config.getProgress().downloadFailed(var6);
            }

            var6.printStackTrace();
            throw var6;
        }
    }
}
