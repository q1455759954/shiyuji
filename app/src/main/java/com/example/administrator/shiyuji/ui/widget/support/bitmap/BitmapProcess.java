package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.example.administrator.shiyuji.support.genereter.KeyGenerator;
import com.example.administrator.shiyuji.ui.widget.support.FileDisk;
import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2019/7/2.
 */

public class BitmapProcess {
    private static final String TAG = "BitmapCache";
    private FileDisk compFielDisk;
    private FileDisk origFileDisk;

    public BitmapProcess(String imageCache) {
        this.compFielDisk = new FileDisk(imageCache + File.separator + "compression");
        this.origFileDisk = new FileDisk(imageCache + File.separator + "originate");
    }

    private byte[] getBitmapFromDiskCache(String url, String key, FileDisk fileDisk, ImageConfig config) throws Exception {
        FileInputStream inputStream = fileDisk.getInputStream(url, key);
        if(inputStream == null) {
            return null;
        } else {
            if(config.getProgress() != null) {
                config.getProgress().sendLength(inputStream.available());
            }

            byte[] buffer = new byte[8192];
            boolean readLen = true;
            int readBytes = 0;

            ByteArrayOutputStream outputStream;
            int readLen1;
            for(outputStream = new ByteArrayOutputStream(); (readLen1 = inputStream.read(buffer)) != -1; outputStream.write(buffer, 0, readLen1)) {
                readBytes += readLen1;
                if(config.getProgress() != null) {
                    config.getProgress().sendProgress((long)readBytes);
                }
            }

            return outputStream.toByteArray();
        }
    }

    public File getOirgFile(String url) {
        String key = KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(url, (ImageConfig)null));
        return this.origFileDisk.getFile(url, key);
    }

    public File getCompressFile(String url, String imageId) {
        ImageConfig config = null;
        if(!TextUtils.isEmpty(imageId)) {
            config = new ImageConfig();
            config.setId(imageId);
        }

        String key = KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(url, config));
        return this.compFielDisk.getFile(url, key);
    }

    public void deleteFile(String url, ImageConfig config) {
        String key = KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(url, config));
        this.compFielDisk.deleteFile(url, key);
        this.origFileDisk.deleteFile(url, key);
    }

    public void writeBytesToOrigDisk(byte[] bs, String url) throws Exception {
        String key = KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(url, (ImageConfig)null));
        OutputStream out = this.origFileDisk.getOutputStream(url, key);
        ByteArrayInputStream in = new ByteArrayInputStream(bs);
        byte[] buffer = new byte[8192];
        boolean len = true;

        int len1;
        while((len1 = in.read(buffer)) != -1) {
            out.write(buffer, 0, len1);
        }

        out.flush();
        in.close();
        out.close();
        this.origFileDisk.renameFile(url, key);
    }

    public void writeBytesToCompressDisk(String url, String key, byte[] bytes) throws Exception {
        OutputStream out = this.compFielDisk.getOutputStream(url, key);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        byte[] buffer = new byte[8192];
        boolean len = true;

        int len1;
        while((len1 = in.read(buffer)) != -1) {
            out.write(buffer, 0, len1);
        }

        out.flush();
        in.close();
        out.close();
        this.compFielDisk.renameFile(url, key);
    }

    public byte[] getBitmapFromCompDiskCache(String url, ImageConfig config) throws Exception {
        String key = KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(url, config));
        return this.getBitmapFromDiskCache(url, key, this.compFielDisk, config);
    }

    public byte[] getBitmapFromOrigDiskCache(String url, ImageConfig config) throws Exception {
        String key = KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(url, (ImageConfig)null));
        return this.getBitmapFromDiskCache(url, key, this.origFileDisk, config);
    }

    public MyBitmap compressBitmap(Context context, byte[] bitmapBytes, String url, int flag, ImageConfig config) throws Exception {
        boolean writeToComp = config.getCorner() > 0;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);
        BitmapUtil.BitmapType bitmapType = BitmapUtil.getType(bitmapBytes);
        MyBitmap myBitmap = null;
        if((flag & 1) != 0) {
            myBitmap = new MyBitmap(BitmapDecoder.decodeSampledBitmapFromByte(context, bitmapBytes), url);
            return myBitmap;
        } else {
            IBitmapCompress bitmapCompress = (IBitmapCompress)config.getBitmapCompress().newInstance();
            myBitmap = bitmapCompress.compress(bitmapBytes, this.getOirgFile(url), url, config, options.outWidth, options.outHeight);
            Bitmap bitmap = myBitmap.getBitmap();
            if(bitmap == null) {
                bitmap = BitmapDecoder.decodeSampledBitmapFromByte(context, bitmapBytes);
            } else {
                writeToComp = true;
            }

            if(bitmapType != BitmapUtil.BitmapType.gif && config.getCorner() > 0) {
                bitmap = BitmapUtil.setImageCorner(bitmap, (float)config.getCorner());
                bitmapType = BitmapUtil.BitmapType.png;
            }

            if(bitmapType == BitmapUtil.BitmapType.gif) {
                writeToComp = true;
            }

            if(writeToComp && config.isCompressCacheEnable()) {
                String key = KeyGenerator.generateMD5(BitmapLoader.getKeyByConfig(url, config));
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(BitmapUtil.BitmapType.png == bitmapType? Bitmap.CompressFormat.PNG: Bitmap.CompressFormat.JPEG, 100, out);
                byte[] bytes = out.toByteArray();
                this.writeBytesToCompressDisk(url, key, bytes);
                if(bitmapType == BitmapUtil.BitmapType.gif) {
//                    Logger.v("BitmapCache", String.format("parse gif image[url=%s,key=%s]", new Object[]{url, key}));
                    bitmap.recycle();
                    bitmap = BitmapDecoder.decodeSampledBitmapFromByte(context, bytes);
                }
            }

            myBitmap.setBitmap(bitmap);
            return myBitmap;
        }
    }
}
