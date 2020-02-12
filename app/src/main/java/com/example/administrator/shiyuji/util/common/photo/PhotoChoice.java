package com.example.administrator.shiyuji.util.common.photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapDecoder;
import com.example.administrator.shiyuji.util.common.utils.FileUtils;
import com.example.administrator.shiyuji.util.common.utils.SystemUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2019/8/4.
 */


public class PhotoChoice {
    public static final int PHONE_IMAGE_REQUEST_CODE = 8888;
    public static final int CAMERA_IMAGE_REQUEST_CODE = 9999;
    public int picMaxDecodeWidth;
    public int picMaxDecodeHeight;
    private Activity mContext;
    private static final Object[][] options = new Object[][]{{null, "本地相册"}, {null, "相机拍摄"}};
    private String tempFilePath;
    private Uri tempFileUri;
    private String tempFileName;
    private PhotoChoice.PhotoChoiceMode mode;
    private PhotoChoice.PhotoChoiceListener choiceListener;

    private PhotoChoice(Activity context) {
        this.tempFilePath = "/sdcard/photoChoice/";
        this.tempFileName = "photodata.o";
        this.mContext = context;
        this.picMaxDecodeWidth = SystemUtils.getScreenWidth(this.mContext) * 5;
        this.picMaxDecodeHeight = SystemUtils.getScreenHeight(this.mContext) * 3;
    }

    public PhotoChoice(Activity context, PhotoChoice.PhotoChoiceListener choiceListener) {
        this(context);
        this.choiceListener = choiceListener;
        this.setPhotoChoice();
    }

    public PhotoChoice(Activity context, PhotoChoice.PhotoChoiceListener choiceListener, String tempFilePath) {
        this(context);
        this.mContext = context;
        this.tempFilePath = tempFilePath;
        this.choiceListener = choiceListener;
        this.setPhotoChoice();
    }

    private void setPhotoChoice() {
        this.setMode(PhotoChoice.PhotoChoiceMode.uriType);
        File file = new File(this.tempFilePath);
        if(!file.exists()) {
            file.mkdirs();
        }

        this.tempFileUri = Uri.fromFile(new File(this.tempFilePath + this.tempFileName));
    }

    public void showChoice(ABaseFragment fragment) {
    }

    public void start(ABaseFragment fragment, int position) {
        Intent intent = null;
        switch(position) {
            case 0:
                intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                if(fragment == null) {
                    this.mContext.startActivityForResult(Intent.createChooser(intent, "请选择文件..."), 8888);
                } else {
                    fragment.startActivityForResult(Intent.createChooser(intent, "请选择文件..."), 8888);
                }
                break;
            case 1:
                intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra("output", this.tempFileUri);
                if(fragment == null) {
                    this.mContext.startActivityForResult(intent, 9999);
                } else {
                    fragment.startActivityForResult(intent, 9999);
                }
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == -1) {
            Bitmap bitmap;
            if(requestCode == 8888) {
                InputStream e;
                switch(this.mode.ordinal()) {
                    case 1:
                        bitmap = null;

                        try {
                            e = this.mContext.getContentResolver().openInputStream(data.getData());
                            byte[] datas = FileUtils.readStreamToBytes(e);
                            bitmap = BitmapDecoder.decodeSampledBitmapFromByte(this.mContext, datas);
                        } catch (Exception var26) {
                            ;
                        }

                        this.choiceListener.choiceBitmap(bitmap);
                        break;
                    case 2:
                        this.choiceListener.choieUri(data.getData(), requestCode);
                        break;
                    case 3:
                        try {
                            e = this.mContext.getContentResolver().openInputStream(data.getData());
                            this.choiceListener.choiceByte(FileUtils.readStreamToBytes(e));
                        } catch (Exception var25) {
                            var25.printStackTrace();
                        }
                }
            } else if(requestCode == 9999) {
                switch(this.mode.ordinal()) {
                    case 1:
                        bitmap = null;

                        try {
                            byte[] e1 = FileUtils.readStreamToBytes(new FileInputStream(this.tempFilePath + this.tempFileName));
                            bitmap = BitmapDecoder.decodeSampledBitmapFromByte(this.mContext, e1);
                        } catch (Exception var23) {
                            var23.printStackTrace();
                        } finally {
                            this.deleteTempFile();
                        }

                        this.choiceListener.choiceBitmap(bitmap);
                        break;
                    case 2:
                        this.choiceListener.choieUri(this.tempFileUri, requestCode);
                        break;
                    case 3:
                        try {
                            this.choiceListener.choiceByte(FileUtils.readFileToBytes(new File(this.tempFilePath + this.tempFileName)));
                        } catch (Exception var21) {
                            var21.printStackTrace();
                        } finally {
                            this.deleteTempFile();
                        }
                }
            }
        } else {
            this.choiceListener.unChoice();
        }

    }

    public void setFileName(String fileName) {
        this.tempFileName = fileName;
        this.tempFileUri = Uri.fromFile(new File(this.tempFilePath + this.tempFileName));
    }

    public void deleteTempFile() {
        File file = new File(this.tempFilePath + this.tempFileName);
        if(file.exists()) {
            file.delete();
        }

    }

    public byte[] parseBitmap(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray();
    }

    public PhotoChoice.PhotoChoiceMode getMode() {
        return this.mode;
    }

    public PhotoChoice setMode(PhotoChoice.PhotoChoiceMode mode) {
        this.mode = mode;
        return this;
    }

    public interface PhotoChoiceListener {
        void choiceByte(byte[] var1);

        void choiceBitmap(Bitmap var1);

        void choieUri(Uri var1, int var2);

        void unChoice();
    }

    public static enum PhotoChoiceMode {
        bitmapType,
        byteType,
        uriType;

        private PhotoChoiceMode() {
        }
    }
}

