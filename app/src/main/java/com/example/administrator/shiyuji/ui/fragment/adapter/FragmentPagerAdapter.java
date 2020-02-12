package com.example.administrator.shiyuji.ui.fragment.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;

/**
 * Created by Administrator on 2019/7/4.
 */


public abstract class FragmentPagerAdapter extends PagerAdapter {
    private static final String TAG = "FragmentPagerAdapter";
    private static final boolean DEBUG = false;
    protected final FragmentManager mFragmentManager;
    protected FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;

    public FragmentPagerAdapter(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    public abstract Fragment getItem(int var1);

    public void startUpdate(ViewGroup container) {
    }

    public Object instantiateItem(ViewGroup container, int position) {
        if(this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }

        this.getItemId(position);
        String name = this.makeFragmentName(position);
        //在mFragmentManager查找是否有这个碎片，有就直接更新，没有则新建并添加到在mFragmentManager查找是否有这个碎片中
        Fragment fragment = this.mFragmentManager.findFragmentByTag(name);
        if(fragment != null) {
            mCurTransaction.attach(fragment);
            this.freshUI(position, fragment);
        } else {
            fragment = this.getItem(position);
            this.mCurTransaction.add(container.getId(), fragment, this.makeFragmentName(position));//添加到mFragmentManager
        }

        if(fragment != this.mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
        }

        if(fragment instanceof FragmentPagerChangeListener) {
            ((FragmentPagerChangeListener)fragment).instantiate(this.makeFragmentName(position));
        }

        return fragment;
    }

    //mFragmentManager找到则直接更新
    protected void freshUI(int position, Fragment fragment) {
        if(fragment instanceof APagingFragment) {
            ((APagingFragment)fragment).refreshUI();
        }
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        if(this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment)object);// 并没有真正释放fragment对象只是detach

//        if(object instanceof APagingFragment) {
//            ((APagingFragment)object).releaseImageViewByIds();
//        }
//
//        if(object instanceof FragmentPagerChangeListener) {
//            ((FragmentPagerChangeListener)object).destroy(this.makeFragmentName(position));
//        }

    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if(fragment != this.mCurrentPrimaryItem) {
            if(this.mCurrentPrimaryItem != null) {
                this.mCurrentPrimaryItem.setMenuVisibility(false);
            }

            if(fragment != null) {
                fragment.setMenuVisibility(true);
            }

            this.mCurrentPrimaryItem = fragment;
        }

    }

    public void finishUpdate(ViewGroup container) {
        if(this.mCurTransaction != null) {
            this.mCurTransaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            this.mFragmentManager.executePendingTransactions();
        }

    }

    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    public Parcelable saveState() {
        return null;
    }

    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    public long getItemId(int position) {
        return (long)position;
    }

    protected abstract String makeFragmentName(int var1);
}
