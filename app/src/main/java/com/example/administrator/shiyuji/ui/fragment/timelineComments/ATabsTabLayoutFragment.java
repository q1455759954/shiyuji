package com.example.administrator.shiyuji.ui.fragment.timelineComments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.ui.fragment.bean.TabItem;

/**
 * Created by Administrator on 2019/7/4.
 */

public abstract class ATabsTabLayoutFragment<T extends TabItem> extends ATabsFragment<T> {
    @ViewInject(
            idStr = "tabLayout"
    )
    TabLayout mTabLayout;

    public ATabsTabLayoutFragment() {
    }

    public int inflateContentView() {
        return R.layout.comm_ui_tabs_tablayout;
    }

    protected  void setupViewPager(Bundle savedInstanceSate) {
        this.setupTabLayout(savedInstanceSate);
    }

    protected void setupTabLayout(Bundle savedInstanceSate) {
        super.setupViewPager(savedInstanceSate);
        this.getTablayout().setTabMode(0);
        this.getTablayout().setTabTextColors(Color.parseColor("#b3ffffff"), -1);
        this.getTablayout().setupWithViewPager(this.getViewPager());
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                ATabsTabLayoutFragment.this.getTablayout().setScrollPosition(ATabsTabLayoutFragment.this.mCurrentPosition, 0.0F, true);
            }
        }, 150L);
    }

    public TabLayout getTablayout() {
        return this.mTabLayout;
    }
}
