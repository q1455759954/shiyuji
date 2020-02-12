package com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment;

import java.util.ArrayList;

import android.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.base.ARecycleViewSwipeRefreshFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.TabItem;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item.AppointmentDefFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.publish.AppointmentFragmentPublish;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.ATabsTabLayoutFragment;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.Utils;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.widgetutil.WidgetUtil;


public class AppointmentFragment extends ATabsTabLayoutFragment<TabItem>
        implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {

    @ViewInject(id=R.id.mytab)
    TabLayout tabLayout;
    @ViewInject(id=R.id.vPager)
    ViewPager viewPager;
    @ViewInject(id=R.id.iv_launch_event)
    ImageView publishImg;


    public static AppointmentFragment newInstance() {
        AppointmentFragment fragment = new AppointmentFragment();
//        Bundle args = new Bundle();
//        args.putString("method", method);
//        fragment.setArguments(args);

        return fragment;
    }

    public static final String ACTION_REFRESH_CMT_GAME = "com.shiyuji.ACTION_REFRESH_CMT_GAME";
    public static final String ACTION_REFRESH_CMT_STUDY = "com.shiyuji.ACTION_REFRESH_CMT_STUDY";
    public static final String ACTION_REFRESH_CMT_EXERCISE = "com.shiyuji.ACTION_REFRESH_CMT_EXERCISE";
    public static final String ACTION_REFRESH_CMT_PLAY = "com.shiyuji.ACTION_REFRESH_CMT_PLAY";


    @Override
    public int inflateContentView() {
        return R.layout.fragment_appointment_main;
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
//        WidgetUtil.setIndicator(tabLayout,35,35);//设置下划线长度
        publishImg.setOnClickListener(publishListener);
//        BaseActivity activity = (BaseActivity) getActivity();
//        activity.getSupportActionBar().setTitle("");// R.string.timeline_detail
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        activity.hideToolbar();

        setHasOptionsMenu(true);
    }

    /**
     * 发布监听器
     */
    ImageView.OnClickListener publishListener = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            AppointmentFragmentPublish.launch(getActivity());

        }
    };

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
        tabLayout.setPadding(Utils.dip2px(getActivity(), 8), tabLayout.getPaddingTop(), tabLayout.getPaddingRight(), tabLayout.getPaddingBottom());
        tabLayout.setTabTextColors(getResources().getColor(R.color.text_54),
                getResources().getColor(R.color.text_80));
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
        tabItems.add(new TabItem("1", "游戏"));
        tabItems.add(new TabItem("2","学习"));
        tabItems.add(new TabItem("3","运动"));
        tabItems.add(new TabItem("4","游玩"));

        return tabItems;
    }

    @Override
    public void onResume() {
        super.onResume();


        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_CMT_GAME);
        filter.addAction(ACTION_REFRESH_CMT_STUDY);
        filter.addAction(ACTION_REFRESH_CMT_EXERCISE);
        filter.addAction(ACTION_REFRESH_CMT_PLAY);
        getActivity().registerReceiver(receiver, filter);

    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(receiver);
    }

    @Override
    protected Fragment newFragment(TabItem bean) {
        // 热门评论
//        if ("0".equals(bean.getType())) {
//            return TimelineHotCommentFragment.newInstance(mStatusContent);
//        }
        // 游戏
        if ("1".equals(bean.getType())) {
            return AppointmentDefFragment.newInstance("game");
        }
        // 学习
        else if ("2".equals(bean.getType())) {
            return AppointmentDefFragment.newInstance("study");
        }
        // 运动
        else if ("3".equals(bean.getType())) {
            return AppointmentDefFragment.newInstance("exercise");
        }
        // 游玩
        else if ("4".equals(bean.getType())) {
            return AppointmentDefFragment.newInstance("play");
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
                        fragment.requestData(APagingFragment.RefreshMode.reset);
                    }
                }

            }, 1000);
        }
    }
    @Override
    public void onClick(View v) {

    }

    /**
     * 发布完成后发布广播，这里接受广播进行更新
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent != null ? intent.getAction() : "";

            if (ACTION_REFRESH_CMT_GAME.equals(action) ) {
                ARecycleViewSwipeRefreshFragment fragment = (ARecycleViewSwipeRefreshFragment) getFragment(0);
                fragment.getSwipeRefreshLayout().setRefreshing(true);

                onRefresh((APagingFragment) getFragment(0));

            }
            else if (ACTION_REFRESH_CMT_STUDY.equals(action)) {
                ARecycleViewSwipeRefreshFragment fragment = (ARecycleViewSwipeRefreshFragment) getFragment(1);
                fragment.getSwipeRefreshLayout().setRefreshing(true);
                onRefresh((APagingFragment) getFragment(1));
            }
            else if (ACTION_REFRESH_CMT_EXERCISE.equals(action)) {
                ARecycleViewSwipeRefreshFragment fragment = (ARecycleViewSwipeRefreshFragment) getFragment(2);
                fragment.getSwipeRefreshLayout().setRefreshing(true);
                onRefresh((APagingFragment) getFragment(2));
            }
            else if (ACTION_REFRESH_CMT_PLAY.equals(action)) {
                ARecycleViewSwipeRefreshFragment fragment = (ARecycleViewSwipeRefreshFragment) getFragment(3);
                fragment.getSwipeRefreshLayout().setRefreshing(true);
                onRefresh((APagingFragment) getFragment(3));
            }
        }

    };


}