package com.example.administrator.shiyuji.ui.widget.support;

import android.text.TextUtils;

import com.example.administrator.shiyuji.support.setting.SettingUtility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2019/7/2.
 */

public class FileDisk {
    private String filePath;
    private final String IMG_SUFFIX;

    public FileDisk(String filePath) {
        if(!TextUtils.isEmpty(SettingUtility.getStringSetting("image_suffix"))) {
            this.IMG_SUFFIX = SettingUtility.getStringSetting("image_suffix");
        } else {
            this.IMG_SUFFIX = "is";
        }

        File file = new File(filePath);
        if(!file.exists()) {
            file.mkdirs();
        }

        this.filePath = filePath;
    }

    public void writeOutStream(byte[] datas, String url, String key) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(datas);
        File file = new File(this.filePath + File.separator + key + "." + this.getImageSuffix(url));
        if(file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[8192];
        boolean len = true;

        int len1;
        while((len1 = in.read(buffer)) != -1) {
            out.write(buffer, 0, len1);
        }

        out.flush();
        out.close();
        in.close();
    }

    public File getFile(String url, String key) {
        return new File(this.filePath + File.separator + key + "." + this.getImageSuffix(url));
    }

    public FileInputStream getInputStream(String url, String key) throws Exception {
        File file = this.getFile(url, key);
        if(file.exists()) {
            if(file.length() == 0L) {
                file.delete();
//                Logger.w("文件已损坏，url = " + url);
                return null;
            } else {
                return new FileInputStream(file);
            }
        } else {
//            Logger.d("getInputStream(String key) not exist");
            return null;
        }
    }

    public OutputStream getOutputStream(String url, String key) throws Exception {
//        Logger.d("getOutputStream(String key)" + this.filePath + File.separator + key + "." + this.getImageSuffix(url) + ".temp");
        File file = new File(this.filePath + File.separator + key + "." + this.getImageSuffix(url) + ".temp");
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return new FileOutputStream(file);
    }

    public void deleteFile(String url, String key) {
        File file = new File(this.filePath + File.separator + key + "." + this.getImageSuffix(url));
        if(file.exists()) {
            file.delete();
        }

    }

    public void renameFile(String url, String key) {
        File file = new File(this.filePath + File.separator + key + "." + this.getImageSuffix(url) + ".temp");
        File newFile = new File(this.filePath + File.separator + key + "." + this.getImageSuffix(url));
        if(file.exists() && file.length() != 0L) {
            if(newFile.exists() && newFile.length() != file.length()) {
//                Logger.v(String.format("原文件已存在不匹配，先删除目标文件，临时文件长度%s, 目标文件长度%s", new Object[]{file.length() + "", newFile.length() + ""}));
                newFile.delete();
            }

            file.renameTo(newFile);
        }

    }

    private String getImageSuffix(String url) {
        return getImageSuffix(url, this.IMG_SUFFIX);
    }

    public static String getImageSuffix(String url, String suffix) {
        if("auto".equals(suffix)) {
            try {
                String temp = url.toLowerCase();
                if(!temp.endsWith(".gif") && !temp.endsWith(".jpg") && !temp.endsWith(".jpeg") && !temp.endsWith(".bmp") && !temp.endsWith(".png")) {
                    return "jpg";
                }

                return url.substring(url.lastIndexOf(".") + 1, url.length());
            } catch (Exception var3) {
                ;
            }
        }

        return suffix;
    }
}
