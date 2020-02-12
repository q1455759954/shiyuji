package com.example.administrator.shiyuji.ui.widget.support.download;

import android.content.Context;

import com.example.administrator.shiyuji.support.genereter.KeyGenerator;
import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;

/**
 * Created by Administrator on 2019/7/4.
 */

public class PictureDownloader implements Downloader {

    @Override
    public byte[] downloadBitmap(Context context, String url, ImageConfig config) throws Exception {
        WebDownloader webDownloader = new WebDownloader();

        String id = KeyGenerator.generateMD5(url);

//        VideoBean videoBean = SinaDB.getDB().selectById(null, VideoBean.class, id);
//        if (videoBean != null) {
//            if (TextUtils.isEmpty(videoBean.getImage())) {
//                int repeat = 8;
//                while (repeat-- > 0) {
//                    try {
//                        VideoService.getPicture(videoBean);
//
//                        Logger.d("VidewDownloader", "video = " + videoBean.getVideoUrl() + ", long = " + videoBean.getLongUrl() + ", short = " + videoBean.getShortUrl() + ", image = " + videoBean.getImage());
//
//                        SinaDB.getDB().update(null, videoBean);
//
//                        break;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                Logger.d(videoBean);
//            }
//
//            if (!TextUtils.isEmpty(videoBean.getImage())) {
//                return webDownloader.downloadBitmap(context, videoBean.getImage().replaceAll("large", "thumbnail"), config);
//            }
//        }

        return null;
    }

}
