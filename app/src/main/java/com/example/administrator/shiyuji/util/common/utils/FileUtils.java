package com.example.administrator.shiyuji.util.common.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.example.administrator.shiyuji.util.common.GlobalContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2019/7/4.
 */

public class FileUtils {


    /**
     * 将文件转化为字节
     * @param file
     * @return
     */
    public static byte[] readFileToBytes(File file) {
        try {
            return readStreamToBytes(new FileInputStream(file));
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static byte[] readStreamToBytes(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        boolean length = true;

        int length1;
        while((length1 = in.read(buffer)) != -1) {
            out.write(buffer, 0, length1);
        }

        out.flush();
        byte[] result = out.toByteArray();
        in.close();
        out.close();
        return result;
    }
    public static boolean writeFile(File file, byte[] bytes) {
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream out = null;
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);

        boolean length;
        try {
            out = new FileOutputStream(file);
            byte[] e = new byte[8192];
            length = true;

            int length1;
            while((length1 = in.read(e)) != -1) {
                out.write(e, 0, length1);
            }

            out.flush();
            return true;
        } catch (Exception var15) {
            var15.printStackTrace();
            length = false;
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch (Exception var14) {
                    ;
                }
            }

        }

        return length;
    }


    public static String getPath(Context context, Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= 19;
        if(isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            String docId;
            String[] split;
            String type;
            if(isExternalStorageDocument(uri)) {
                docId = DocumentsContract.getDocumentId(uri);
                split = docId.split(":");
                type = split[0];
                if("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else {
                if(isDownloadsDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri);
                    Uri split1 = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId).longValue());
                    return getDataColumn(context, split1, (String)null, (String[])null);
                }

                if(isMediaDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri);
                    split = docId.split(":");
                    type = split[0];
                    Uri contentUri = null;
                    if("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if("video".equals(type)) {
                        contentUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if("audio".equals(type)) {
                        contentUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, "_id=?", selectionArgs);
                }
            }
        } else {
            if("content".equalsIgnoreCase(uri.getScheme())) {
                if(isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }

                return getDataColumn(context, uri, (String)null, (String[])null);
            }

            if("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = new String[]{"_data"};

        String var8;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, (String)null);
            if(cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            int index = cursor.getColumnIndexOrThrow("_data");
            var8 = cursor.getString(index);
        } finally {
            if(cursor != null) {
                cursor.close();
            }

        }

        return var8;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
