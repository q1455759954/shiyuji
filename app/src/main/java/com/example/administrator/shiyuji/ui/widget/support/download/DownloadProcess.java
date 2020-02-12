package com.example.administrator.shiyuji.ui.widget.support.download;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Administrator on 2019/7/2.
 */

public abstract class DownloadProcess {
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case 0:
                    DownloadProcess.this.receiveLength(Long.parseLong(msg.obj.toString()));
                    break;
                case 1:
                    DownloadProcess.this.receiveProgress(Long.parseLong(msg.obj.toString()));
                    break;
                case 2:
                    Object[] tag = (Object[])((Object[])msg.obj);
                    DownloadProcess.this.prepareDownload(tag[0].toString());
                    break;
                case 3:
                    DownloadProcess.this.finishedDownload((byte[])((byte[])msg.obj));
                    break;
                case 4:
                    DownloadProcess.this.downloadFailed((Exception)msg.obj);
            }

        }
    };

    public DownloadProcess() {
    }

    public void sendLength(int length) {
        Message msg = this.mHandler.obtainMessage();
        msg.what = 0;
        msg.obj = Integer.valueOf(length);
        msg.sendToTarget();
    }

    public void sendProgress(long progress) {
        Message msg = this.mHandler.obtainMessage();
        msg.what = 1;
        msg.obj = Long.valueOf(progress);
        msg.sendToTarget();
    }

    void sendPrepareDownload(String url) {
        Message msg = this.mHandler.obtainMessage();
        msg.what = 2;
        msg.obj = new Object[]{url};
        msg.sendToTarget();
    }

    public void sendFinishedDownload(byte[] bytes) {
        Message msg = this.mHandler.obtainMessage();
        msg.what = 3;
        msg.obj = bytes;
        msg.sendToTarget();
    }

    void sendException(Exception e) {
        Message msg = this.mHandler.obtainMessage();
        msg.what = 4;
        msg.obj = e;
        msg.sendToTarget();
    }

    public void receiveLength(long length) {
    }

    public void receiveProgress(long progressed) {
    }

    public void prepareDownload(String url) {
    }

    public void finishedDownload(byte[] bytes) {
    }

    public void downloadFailed(Exception e) {
    }
}
