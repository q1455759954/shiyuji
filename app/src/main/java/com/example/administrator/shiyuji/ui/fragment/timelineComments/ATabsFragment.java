package com.example.administrator.shiyuji.ui.fragment.timelineComments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;


import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.activity.base.ActivityHelper;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.home.HotFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.LifeFragment;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.ui.fragment.adapter.FragmentPagerAdapter;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.TabItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2019/7/4.
 */

public abstract class ATabsFragment<T extends TabItem> extends ABaseFragment implements ViewPager.OnPageChangeListener {
    static final String TAG = "AFragment-Tabs";
    public static final String SET_INDEX = "org.aisen.android.ui.SET_INDEX";
    @ViewInject(
            idStr = "viewPager"
    )
    ViewPager mViewPager;
    FragmentPagerAdapter mInnerAdapter;
    ArrayList<T> mItems;
    Map<String, Fragment> fragments;
    int mCurrentPosition = 0;
    private boolean isRetainFragments = false;

    public ATabsFragment() {
    }

    public int inflateContentView() {
        return R.layout.comm_ui_tabs;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.mCurrentPosition = this.getViewPager().getCurrentItem();
        outState.putSerializable("items", this.mItems);
        outState.putInt("current", this.mCurrentPosition);
        this.isRetainFragments = true;
    }

    protected void layoutInit(LayoutInflater inflater, final Bundle savedInstanceState) {
        super.layoutInit(inflater, savedInstanceState);
        this.mItems = savedInstanceState == null?this.generateTabs():(ArrayList)savedInstanceState.getSerializable("items");

        this.mCurrentPosition = savedInstanceState == null?0:savedInstanceState.getInt("current");
        if(!this.asyncTabInit()) {
            if(this.delayTabInit() == 0) {
                this.setTabInit(savedInstanceState);
            } else {
                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        ATabsFragment.this.setTabInit(savedInstanceState);
                    }
                }, (long)this.delayTabInit());
            }
        }

    }

    public int getmCurrentPosition() {
        return mCurrentPosition;
    }

    public void setmCurrentPosition(int mCurrentPosition) {
        this.mCurrentPosition = mCurrentPosition;
    }

    protected void setTabInit(Bundle savedInstanceSate) {
        if(this.getActivity() != null) {
            if(!(this.getActivity() instanceof BaseActivity) || !((BaseActivity)this.getActivity()).isDestory()) {
                if(savedInstanceSate == null) {
                    if(this.getArguments() != null && this.getArguments().containsKey("org.aisen.android.ui.SET_INDEX")) {
                        this.mCurrentPosition = Integer.parseInt(this.getArguments().getSerializable("org.aisen.android.ui.SET_INDEX").toString());
                    } else if(this.configLastPositionKey() != null) {
                        String i = ActivityHelper.getShareData(GlobalContext.getInstance(), "PagerLastPosition" + this.configLastPositionKey(), "");
                        if(!TextUtils.isEmpty(i)) {
                            for(int fragment = 0; fragment < this.mItems.size(); ++fragment) {
                                TabItem item = (TabItem)this.mItems.get(fragment);
                                if(i.equals(item.getType())) {
                                    this.mCurrentPosition = fragment;
                                    break;
                                }
                            }
                        }
                    }
                }

//                Logger.d("AFragment-Tabs", "CurrentPosition " + this.mCurrentPosition);
                this.fragments = new HashMap();
                if(savedInstanceSate == null) {
                    for(int var5 = 0; var5 < this.mItems.size(); ++var5) {
                        Fragment var6 = this.getActivity().getFragmentManager().findFragmentByTag(this.makeFragmentName(var5));
                        if(var6 != null) {
                            this.getActivity().getFragmentManager().beginTransaction().remove(var6).commit();
                        }
                    }
                }

                this.setupViewPager(savedInstanceSate);
            }
        }
    }

    /**
     * 设置viewPager，设置当前的页面，子类实现了tableLayout与viewPage的绑定
     * @param savedInstanceSate
     */
    protected void setupViewPager(Bundle savedInstanceSate) {
        if (this instanceof HotFragment || this instanceof LifeFragment){
            this.mInnerAdapter = new ATabsFragment.InnerAdapter(this.getChildFragmentManager());
        }else {
            this.mInnerAdapter = new ATabsFragment.InnerAdapter(this.getFragmentManager());
        }

        this.getViewPager().setOffscreenPageLimit(0);
        this.getViewPager().setAdapter(this.mInnerAdapter);
        if(this.mCurrentPosition >= this.mInnerAdapter.getCount()) {
            this.mCurrentPosition = 0;
        }

        this.getViewPager().setCurrentItem(this.mCurrentPosition);
        this.getViewPager().addOnPageChangeListener(this);
    }



    public void onPageScrolled(int i, float v, int i2) {
    }

    public void onPageSelected(int position) {
        this.mCurrentPosition = position;
        if(this.configLastPositionKey() != null) {
            ActivityHelper.putShareData(GlobalContext.getInstance(), "PagerLastPosition" + this.configLastPositionKey(), ((TabItem)this.mItems.get(position)).getType());
        }

        Fragment fragment = this.getCurrentFragment();
        if(fragment instanceof ATabsFragment.ITabInitData) {
            ((ATabsFragment.ITabInitData)fragment).onTabRequestData();
        }

    }

    public void onPageScrollStateChanged(int position) {
    }

    public String makeFragmentName(int position) {
        return String.valueOf(((TabItem)this.mItems.get(position)).getTitle().hashCode());
    }

    public List<T> getTabItems() {
        return this.mItems;
    }

    protected void setTabItems(ArrayList<T> items) {
        this.mItems = items;
    }

    public PagerAdapter getPageAdapter() {
        return this.mInnerAdapter;
    }

    public void setmInnerAdapter(FragmentPagerAdapter mInnerAdapter) {
        this.mInnerAdapter = mInnerAdapter;
    }

    protected String configLastPositionKey() {
        return null;
    }

    protected abstract ArrayList<T> generateTabs();

    protected abstract Fragment newFragment(T var1);

    protected int delayTabInit() {
        return 0;
    }

    protected boolean asyncTabInit() {
        return false;
    }

    public void onDestroy() {
        try {
            if(!this.isRetainFragments) {
                this.destoryFragments();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        super.onDestroy();
    }

    public Fragment getCurrentFragment() {
        return this.getViewPager() != null && this.mInnerAdapter != null && this.mInnerAdapter.getCount() >= this.mCurrentPosition?(Fragment)this.fragments.get(this.makeFragmentName(this.mCurrentPosition)):null;
    }

    public Fragment getFragment(String tabTitle) {
        if(this.fragments != null && !TextUtils.isEmpty(tabTitle)) {
            for(int i = 0; i < this.mItems.size(); ++i) {
                if(tabTitle.equals(((TabItem)this.mItems.get(i)).getTitle())) {
                    return (Fragment)this.fragments.get(this.makeFragmentName(i));
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public Fragment getFragment(int index) {
        return this.mItems.size() > index?(Fragment)this.fragments.get(this.makeFragmentName(index)):null;
    }

    public Map<String, Fragment> getFragments() {
        return this.fragments;
    }

    public ViewPager getViewPager() {
        return this.mViewPager;
    }

    public interface ITabInitData {
        void onTabRequestData();
    }

    public class InnerAdapter extends FragmentPagerAdapter {
        public InnerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(this.makeFragmentName(position));
            if(fragment == null) {
                fragment = newFragment(mItems.get(position));
                fragments.put(this.makeFragmentName(position), fragment);
            }

            return fragment;
        }

        protected void freshUI(int position, Fragment fragment) {
            super.freshUI(position, fragment);
            if(!fragments.containsValue(fragment)) {
                fragments.put(this.makeFragmentName(position), fragment);
            }

        }

        public int getCount() {
            return ATabsFragment.this.mItems.size();
        }

        public CharSequence getPageTitle(int position) {
            return ((TabItem)ATabsFragment.this.mItems.get(position)).getTitle();
        }

        protected String makeFragmentName(int position) {
            return ATabsFragment.this.makeFragmentName(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
//
//            Fragment fragment = this.mFragmentManager.findFragmentByTag(this.makeFragmentName(position));
//            if (fragment != null)
//                mCurTransaction.remove(fragment);
        }
    }

    /**
     * onDestory时调用
     */
    protected void destoryFragments() {
        if(this.getActivity() != null) {
            if(this.getActivity() instanceof BaseActivity) {
                BaseActivity e = (BaseActivity)this.getActivity();
                if(e.isDestory()) {
                    return;
                }
            }
            try {
                FragmentTransaction e1 = null;
                if (ATabsFragment.this instanceof HotFragment){
                    e1 = this.getChildFragmentManager().beginTransaction();
                }else {
                    e1 = this.getFragmentManager().beginTransaction();
                }

                Set keySet = this.fragments.keySet();
                Iterator var3 = keySet.iterator();

                while(var3.hasNext()) {
                    String key = (String)var3.next();
                    if(this.fragments.get(key) != null) {
                        e1.remove((Fragment)this.fragments.get(key));
                    }
                }

                e1.commit();
            } catch (Throwable var5) {
            }
        }

    }
}
