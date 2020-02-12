package com.example.administrator.shiyuji.support.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.administrator.shiyuji.support.action.IAction;

/**
 * Created by Administrator on 2019/8/3.
 */


public abstract class APermissionsAction extends IAction implements IPermissionsObserver {
    public static final String TAG = "Permission";
    private IPermissionsSubject subject;
    private String permission;
    private int requestCode;

    public APermissionsAction(Activity context, IAction parent, IPermissionsSubject subject, String permission) {
        super(context, parent);
        this.subject = subject;
        this.permission = permission;
        this.requestCode = permission.hashCode();
    }

    protected boolean interrupt() {
        boolean interrupt = super.interrupt();
        if(this.requestCode != 0 && Build.VERSION.SDK_INT >= 23) {
            if(0 == ContextCompat.checkSelfPermission(this.getContext(), this.permission)) {
//                Logger.d("Permissionon", "已经授予了权限, permission = %s", new Object[]{this.permission});
            } else if(-1 == ContextCompat.checkSelfPermission(this.getContext(), this.permission)) {
                interrupt = true;
//                Logger.d("Permission", "%s permission = %s", new Object[]{"PERMISSION_DENIED", this.permission});
                this.doInterrupt();
            }
        }

        return interrupt;
    }

    public void doInterrupt() {
        if(!this.handlePermissionNone()) {
//            Logger.d("Permission", "handlePermissionNone(false)");
            this.requestPermission();
        } else {
//            Logger.d("Permission", "handlePermissionNone(true)");
        }

    }

    protected boolean handlePermissionNone() {
        return false;
    }

    protected void onPermissionDenied(boolean alwaysDenied) {
    }

    @TargetApi(23)
    protected void requestPermission() {
        if(this.subject != null) {
            this.subject.attach(this);
        }

//        Logger.d("Permission", "requestPermission(%s)", new Object[]{this.permission});

        try {
            this.getContext().requestPermissions(new String[]{this.permission}, this.requestCode);
        } catch (IllegalArgumentException var2) {
//            Logger.printExc(APermissionsAction.class, var2);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        Logger.d("Permission", "onRequestPermissionsResult , requestCode = " + requestCode);
        if(grantResults != null && grantResults.length > 0) {
            for(int i = 0; i < permissions.length; ++i) {
//                Logger.d("Permission", "requestCode = %d, permission = %s, grantResult = %d", new Object[]{Integer.valueOf(requestCode), permissions[i], Integer.valueOf(grantResults[i])});
            }
        }

        if(this.subject != null) {
            this.subject.detach(this);
        }

        if(requestCode == this.requestCode) {
            if(permissions != null && permissions.length > 0 && this.permission.equals(permissions[0]) && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
                this.run();
                return;
            }

            if(ActivityCompat.shouldShowRequestPermissionRationale(this.getContext(), this.permission)) {
//                Logger.d("Permission", "onPermissionDenied(false)");
                this.onPermissionDenied(false);
            } else {
//                Logger.d("Permission", "onPermissionDenied(true)");
                this.onPermissionDenied(true);
            }
        }

    }
}

