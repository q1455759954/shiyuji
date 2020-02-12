package com.example.administrator.shiyuji.support.permissions;

/**
 * Created by Administrator on 2019/8/3.
 */

public interface IPermissionsSubject {
    void attach(IPermissionsObserver var1);

    void detach(IPermissionsObserver var1);

    void notifyActivityResult(int var1, String[] var2, int[] var3);
}
