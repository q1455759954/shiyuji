package com.example.administrator.shiyuji.sdk.http;

import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.io.File;

/**
 * Created by Administrator on 2019/7/8.
 */

public interface IHttpUtility {

    <T> T doGet(HttpConfig var1, Setting var2, Params var3, Class<T> var4) throws TaskException;

    <T> T doPost(HttpConfig var1, Setting var2, Params var3, Params var4, Object var5, Class<T> var6) throws TaskException;

    <T> T doPostFiles(HttpConfig var1, Setting var2, Params var3, Params var4, IHttpUtility.MultipartFile[] var5, Class<T> var6) throws TaskException;

    public static class MultipartFile {
        private final String contentType;
        private final File file;
        private final String key;
        private final byte[] bytes;
        private OnFileProgress callback;

        public MultipartFile(String contentType, String key, File file) {
            this.key = key;
            this.contentType = contentType;
            this.file = file;
            this.bytes = null;
        }

        public MultipartFile(String contentType, String key, byte[] bytes) {
            this.key = key;
            this.contentType = contentType;
            this.bytes = bytes;
            this.file = null;
        }

        public String getContentType() {
            return this.contentType;
        }

        public File getFile() {
            return this.file;
        }

        public byte[] getBytes() {
            return this.bytes;
        }

        public String getKey() {
            return this.key;
        }

        public void setOnProgress(OnFileProgress callback) {
            this.callback = callback;
        }

        public OnFileProgress getOnProgress() {
            return this.callback;
        }
    }
}
