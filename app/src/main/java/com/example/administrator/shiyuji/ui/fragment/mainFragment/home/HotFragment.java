package com.example.administrator.shiyuji.ui.fragment.mainFragment.home;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.publish.PublishActivity;
import com.example.administrator.shiyuji.ui.fragment.adapter.FragmentPagerAdapter;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.TabItem;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineDefFragment;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.ATabsFragment;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.ATabsTabLayoutFragment;
import com.example.administrator.shiyuji.ui.widget.AspectImageView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.Utils;
import com.example.administrator.shiyuji.util.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019/9/9.
 */

public class HotFragment extends ATabsTabLayoutFragment<TabItem>
        implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {

    @ViewInject(id= R.id.tabLayout)
    TabLayout tabLayout;
    @ViewInject(id=R.id.viewPager)
    ViewPager viewPager;


    public static HotFragment newInstance() {
        HotFragment fragment = new HotFragment();
//        Bundle args = new Bundle();
//        args.putString("method", method);
//        fragment.setArguments(args);

        return fragment;
    }

    public int inflateContentView() {
        return R.layout.comm_ui_tabs_tablayout;
    }
    public int inflateActivityContentView() {return -1;}


    @Override
    public TabLayout getTablayout() {
        return this.tabLayout;
    }

    @Override
    public ViewPager getViewPager() {
        return this.viewPager;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mStatusContent = savedInstanceState != null ? (StatusContent) savedInstanceState.getSerializable("status")
//                : (StatusContent) getArguments().getSerializable("status");
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        bizFragment = BizFragment.createBizFragment(this);


        //设置tablayout位置居中
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
//        BaseActivity activity = (BaseActivity) getActivity();
//        activity.getSupportActionBar().setTitle("");// R.string.timeline_detail
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        activity.hideToolbar();

        setHasOptionsMenu(true);
    }


    private Handler mHandler = new Handler();

    Runnable initCurrentFragment = new Runnable() {

        @Override
        public void run() {
            if (getCurrentFragment() != null && getCurrentFragment() instanceof APagingFragment &&
                    ((APagingFragment) getCurrentFragment()).getRefreshView() != null) {
//                laySroll.setRefreshView(((APagingFragment) getCurrentFragment()).getRefreshView());
            }
            else {
                mHandler.postDelayed(initCurrentFragment, 100);
            }
        }

    };

    /**
     * 设置TabLayout
     * @param savedInstanceSate
     */
    @Override
    protected void setupTabLayout(Bundle savedInstanceSate) {
        super.setupTabLayout(savedInstanceSate);

        TabLayout tabLayout = getTablayout();
        tabLayout.setPadding(Utils.dip2px(getActivity(), 5), tabLayout.getPaddingTop(), tabLayout.getPaddingRight(), tabLayout.getPaddingBottom());
        tabLayout.setTabTextColors(getResources().getColor(R.color.text_55),
                getResources().getColor(R.color.bg_followers_counter));
        tabLayout.setSelectedTabIndicatorHeight(0);

    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        if (getCurrentFragment() != null && getCurrentFragment() instanceof APagingFragment &&
                ((APagingFragment) getCurrentFragment()).getRefreshView() != null) {
//            laySroll.setRefreshView(((APagingFragment) getCurrentFragment()).getRefreshView());
        }

        // 切换了Page就显示Fab
//        BizFragment.createBizFragment(getActivity()).getFabAnimator().show();
    }

    @Override
    protected ArrayList<TabItem> generateTabs() {

        ArrayList<TabItem> tabItems = new ArrayList<>();

//        if (mStatusContent.getComments_count() >= HOT_CMT_SHOW_MIN_COUNT && !TextUtils.isEmpty(AppContext.getAccount().getCookie())) {
//            tabItems.add(new TabItem("0", getString(R.string.comment_hot)));
//        }
        tabItems.add(new TabItem("1", "推荐"));
        tabItems.add(new TabItem("2","生活"));
        tabItems.add(new TabItem("3","游戏"));
        tabItems.add(new TabItem("4","搞笑"));

        return tabItems;
    }

    @Override
    protected Fragment newFragment(TabItem bean) {

        //推荐
        if ("1".equals(bean.getType())) {
//            recommend
            return TimelineDefFragment.newInstance("friendshipGroupsTimeline","recommend");
        }
        // 生活
        else if ("2".equals(bean.getType())) {
            return TimelineDefFragment.newInstance("friendshipGroupsTimeline","life");
        }
        // 游戏
        else if ("3".equals(bean.getType())) {
            return TimelineDefFragment.newInstance("friendshipGroupsTimeline","game");
        }
        // 搞笑
        else if ("4".equals(bean.getType())) {
            return TimelineDefFragment.newInstance("friendshipGroupsTimeline","fun");
        }

        return null;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    @Override
    public void onRefresh() {
        APagingFragment fragment = (APagingFragment) getCurrentFragment();

        onRefresh(fragment);
    }
    private void onRefresh(final APagingFragment fragment) {
        if (!isDestory()) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (fragment != null && !fragment.isRefreshing()) {
                        fragment.requestData(APagingFragment.RefreshMode.refresh);
                    }
                }

            }, 1000);
        }
    }
    @Override
    public void onClick(View v) {

    }

}
