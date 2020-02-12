package com.example.administrator.shiyuji.ui.widget.support.download;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapDecoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Administrator on 2019/8/3.
 */

public class SdcardDownloader implements Downloader {
    public SdcardDownloader() {
    }

    public byte[] downloadBitmap(Context context, String url, ImageConfig config) throws Exception {
        try {
            File e = new File(url);
            if(e.exists()) {
                DownloadProcess process = config.getProgress();
                if(process != null) {
                    process.prepareDownload(url);
                }

                ByteArrayOutputStream out;
                if(config.getMaxHeight() <= 0 && config.getMaxWidth() <= 0) {
                    FileInputStream in1 = new FileInputStream(new File(url));
                    if(process != null) {
                        process.sendLength(in1.available());
                    }

                    out = new ByteArrayOutputStream();
                    byte[] buffer1 = new byte[8192];
                    boolean length1 = true;

                    int length2;
                    for(long readBytes = 0L; (length2 = in1.read(buffer1)) != -1; out.write(buffer1, 0, length2)) {
                        readBytes += (long)length2;
                        if(process != null) {
                            process.sendProgress(readBytes);
                        }
                    }

                    out.flush();
                    byte[] result = out.toByteArray();
                    in1.close();
                    out.close();
                    if(process != null) {
                        process.sendFinishedDownload(result);
                    }

                    return result;
                } else {
                    Bitmap in = BitmapDecoder.decodeSampledBitmapFromFile(e.getAbsolutePath(), config.getMaxWidth(), config.getMaxHeight());
                    out = new ByteArrayOutputStream();
                    boolean buffer = url.toLowerCase().endsWith("png");
                    in.compress(buffer? Bitmap.CompressFormat.PNG: Bitmap.CompressFormat.JPEG, 100, out);
                    byte[] length = out.toByteArray();
                    out.close();
                    if(process != null) {
                        process.sendLength(length.length);
                        process.sendProgress((long)length.length);
                        process.sendFinishedDownload(length);
                    }

//                    Logger.w("直接解析sd卡图片，压缩尺寸");
                    return length;
                }
            } else {
                if(config.getProgress() != null) {
                    config.getProgress().downloadFailed((Exception)null);
                }

                throw new Exception("");
            }
        } catch (Exception var13) {
            var13.printStackTrace();
            if(config.getProgress() != null) {
                config.getProgress().sendException(var13);
            }

            throw new Exception(var13.getCause());
        }
    }
}
