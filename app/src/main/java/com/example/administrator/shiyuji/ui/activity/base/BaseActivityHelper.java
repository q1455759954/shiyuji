package com.example.administrator.shiyuji.ui.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.example.administrator.shiyuji.support.permissions.IPermissionsObserver;
import com.example.administrator.shiyuji.support.permissions.IPermissionsSubject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 实现了权限观察者模式主题
 * Created by Administrator on 2019/8/3.
 */


public class BaseActivityHelper implements IPermissionsSubject {
    private List<IPermissionsObserver> observers;
    private BaseActivity mActivity;

    public BaseActivityHelper() {
    }

    protected void bindActivity(BaseActivity activity) {
        this.mActivity = activity;
    }

    protected BaseActivity getActivity() {
        return this.mActivity;
    }

    protected void onCreate(Bundle savedInstanceState) {
        this.observers = new ArrayList();
    }

    public void onPostCreate(Bundle savedInstanceState) {
    }

    public View findViewById(int id) {
        return this.mActivity.findViewById(id);
    }

    protected void onStart() {
    }

    protected void onRestart() {
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    protected void onStop() {
    }

    public void onDestroy() {
    }

    public void finish() {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    protected boolean onHomeClick() {
        return false;
    }

    public boolean onBackClick() {
        return false;
    }

    protected int configTheme() {
        return 0;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public void attach(IPermissionsObserver observer) {
        if(observer != null && !this.observers.contains(observer)) {
            this.observers.add(observer);
        }

    }

    public void detach(IPermissionsObserver observer) {
        if(observer != null && !this.observers.contains(observer)) {
            this.observers.remove(observer);
        }

    }

    public void notifyActivityResult(int requestCode, String[] permissions, int[] grantResults) {
        Iterator var4 = this.observers.iterator();

        while(var4.hasNext()) {
            IPermissionsObserver observer = (IPermissionsObserver)var4.next();
            observer.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        this.notifyActivityResult(requestCode, permissions, grantResults);
    }
}
