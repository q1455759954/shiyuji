package com.example.administrator.shiyuji.support.permissions;

import android.Manifest;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.support.action.IAction;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;

/**
 * Created by Administrator on 2019/8/3.
 */

public class SdcardPermissionAction extends APermissionsAction {

    public SdcardPermissionAction(BaseActivity context, IAction parent) {
        super(context, parent, context.getActivityHelper(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onPermissionDenied(boolean alwaysDenied) {
        if (alwaysDenied) {
            ((BaseActivity) getContext()).showMessage(R.string.alwaysdenied_sdcard_permission);
        }
        else {
            ((BaseActivity) getContext()).showMessage(R.string.cancel_sdcard_permission);
        }
    }

}
