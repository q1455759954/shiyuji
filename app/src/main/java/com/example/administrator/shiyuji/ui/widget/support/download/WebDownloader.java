package com.example.administrator.shiyuji.ui.widget.support.download;

import android.content.Context;
import android.util.Log;

import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2019/7/2.
 */

public class WebDownloader implements Downloader {
    public WebDownloader() {
    }

    public byte[] downloadBitmap(Context context, String url, ImageConfig config) throws Exception {
//        Logger.v(url);

        try {
            ByteArrayOutputStream e = new ByteArrayOutputStream();
            DownloadProcess progress = config.getProgress();
            if(progress != null) {
                progress.sendPrepareDownload(url);
            }
            Request request = (new Request.Builder()).url(url).build();
            Response response = GlobalContext.getOkHttpClient().newCall(request).execute();
            if(response.code() != 200 && response.code() != 206) {
                throw new TaskException(String.valueOf(TaskException.TaskError.failIOError));
            } else {
                int length = 0;

                try {
                    String in = response.header("Content-Length");
                    length = Integer.parseInt(in);
                } catch (Exception var14) {
                    ;
                }

                if(progress != null) {
                    progress.sendLength(length);
                }

                InputStream in1 = response.body().byteStream();

                byte[] buffer = new byte[8192];
                boolean readLen = true;

                int readLen1;
                for(int readBytes = 0; (readLen1 = in1.read(buffer)) != -1; e.write(buffer, 0, readLen1)) {
                    readBytes += readLen1;
                    if(progress != null) {
                        progress.sendProgress((long)readBytes);
                    }
                }
                byte[] bs = e.toByteArray();
                if(length != 0 && bs.length != length) {
                    return null;
                } else {
                    in1.close();
                    e.close();
                    return bs;
                }
            }
        } catch (Exception var15) {
            var15.printStackTrace();
            if(config.getProgress() != null) {
                config.getProgress().sendException(var15);
            }

            throw new Exception(var15.getCause());
        }
    }
}
