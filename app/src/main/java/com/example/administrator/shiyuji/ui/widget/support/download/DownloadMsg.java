//package com.example.administrator.shiyuji.ui.widget.support.download;
//
//import android.net.Uri;
//
//import java.io.Serializable;
//
///**
// * Created by Administrator on 2019/7/6.
// */
//
//public final class DownloadMsg implements Serializable {
//    private static final long serialVersionUID = 1749484931688943470L;
//    private final String key;
//    private final String uri;
//    private final String filePath;
//    private final int control;
//    private final int status;
//    private final String errorMsg;
//    private final long totalBytes;
//    private final long currentBytes;
//
//    DownloadMsg(String key) {
//        this.key = key;
//        this.uri = null;
//        this.filePath = null;
//        this.control = -1;
//        this.status = -1;
//        this.errorMsg = null;
//        this.totalBytes = 0L;
//        this.currentBytes = 0L;
//    }
//
//    DownloadMsg(DownloadInfo info) {
//        this.key = info.mKey;
//        this.uri = info.mUri;
//        this.filePath = info.mFilePath;
//        this.control = info.mControl;
//        this.status = info.mStatus;
//        this.errorMsg = info.mErrorMsg;
//        this.totalBytes = info.mTotalBytes;
//        this.currentBytes = info.mCurrentBytes;
//    }
//
//    public boolean isNull() {
//        return this.uri == null && this.filePath == null;
//    }
//
//    public String getKey() {
//        return this.key;
//    }
//
//    public int getStatus() {
//        return this.translateStatus(this.status);
//    }
//
//    public Uri getUri() {
//        return Uri.parse(this.uri);
//    }
//
//    public Uri getFilePath() {
//        return Uri.parse(this.filePath);
//    }
//
//    public long getCurrent() {
//        return this.currentBytes;
//    }
//
//    public long getTotal() {
//        return this.totalBytes;
//    }
//
//    public long getReason() {
//        switch(this.translateStatus(this.status)) {
//            case 16:
//                return this.getErrorCode(this.status);
//            case 32:
//                return this.getPausedReason(this.status);
//            default:
//                return 0L;
//        }
//    }
//
//    public String status2String() {
//        return Impl.statusToString(this.status);
//    }
//
//    private long getPausedReason(int status) {
//        switch(status) {
//            case 194:
//                return 1L;
//            case 195:
//                return 2L;
//            case 196:
//                return 3L;
//            default:
//                return 4L;
//        }
//    }
//
//    private long getErrorCode(int status) {
//        if(400 <= status && status < 488 || 500 <= status && status < 600) {
//            return (long)status;
//        } else {
//            switch(status) {
//                case 198:
//                    return 1006L;
//                case 488:
//                    return 1009L;
//                case 489:
//                    return 1008L;
//                case 492:
//                    return 1001L;
//                case 493:
//                case 494:
//                    return 1002L;
//                case 495:
//                    return 1004L;
//                case 497:
//                    return 1005L;
//                default:
//                    return 1000L;
//            }
//        }
//    }
//
//    private int translateStatus(int status) {
//        return Impl.translateStatus(status);
//    }
//}
