package com.example.administrator.shiyuji.support.action;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;


import java.util.Random;

/**
 * Created by wangdan on 15/5/1.
 */
public class WebLoginAction extends IAction {

    BizFragment mBizFragment;

    int requestCode;

    public WebLoginAction(Activity context, BizFragment bizFragment) {
        super(context, null);

        requestCode = new Random().nextInt(1000);
        mBizFragment = bizFragment;
    }

    @Override
    protected boolean interrupt() {
//        if (TextUtils.isEmpty(AppContext.getAccount().getCookie())) {
//            doInterrupt();
//
//            return true;
//        }

        return false;
    }

    @Override
    public void doInterrupt() {
//        new MaterialDialog.Builder(getContext()).content(getContext().getString(R.string.acount_timeout))
//                .negativeText(getContext().getString(R.string.no))
//                .positiveText(getContext().getString(R.string.yes))
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        mBizFragment.requestWebLogin(WebLoginAction.this);
//                    }
//
//                })
//                .show();
    }

    @Override
    public void doAction() {
        if (getChild() != null) {
            getChild().run();
        }
    }
    
    public int getRequestCode() {
        return requestCode;
    }

}
