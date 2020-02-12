package com.example.administrator.shiyuji.ui.fragment.mainFragment.life;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.support.action.IAction;
import com.example.administrator.shiyuji.support.permissions.SdcardPermissionAction;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.publish.PublishActivity;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.TabItem;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.home.HotFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.item.LifeDefFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.publish.LifeFragmentPublish;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.publish.WorkPublish;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.like.LikeDefFragment;
import com.example.administrator.shiyuji.ui.fragment.picturepick.PicturePickFragment;
import com.example.administrator.shiyuji.ui.fragment.publish.APublishFragment;
import com.example.administrator.shiyuji.ui.fragment.search.SearchFragment;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineDefFragment;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.ATabsTabLayoutFragment;
import com.example.administrator.shiyuji.ui.widget.AspectImageView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.Utils;
import com.example.administrator.shiyuji.util.annotation.ViewInject;

import java.util.ArrayList;

public class LifeFragment extends ATabsTabLayoutFragment<TabItem>
        implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {

    @ViewInject(id=R.id.tabLayout)
    TabLayout tabLayout;
    @ViewInject(id=R.id.viewPager)
    ViewPager viewPager;
    @ViewInject(id=R.id.publish)
    AspectImageView publishImg;
    @ViewInject(id=R.id.search_icon)
    AspectImageView search_icon;

    public static LifeFragment newInstance() {
        LifeFragment fragment = new LifeFragment();
//        Bundle args = new Bundle();
//        args.putString("method", method);
//        fragment.setArguments(args);

        return fragment;
    }

    public int inflateContentView() {
        return R.layout.fragment_home;
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
        publishImg.setOnClickListener(publishListener);
//        BaseActivity activity = (BaseActivity) getActivity();
//        activity.getSupportActionBar().setTitle("");// R.string.timeline_detail
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        activity.hideToolbar();

        setHasOptionsMenu(true);

        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment.launch(getActivity(),null, "");
            }
        });
    }

    /**
     * 发布监听器
     */
    ImageView.OnClickListener publishListener = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {

            showTypeDialog();

        }
    };

    private void showTypeDialog() {
        new IAction(getActivity(), new SdcardPermissionAction(((BaseActivity) getActivity()), null)) {

            @Override
            public void doAction() {
                new MaterialDialog.Builder(getActivity())
                        .items(R.array.publish_life)
                        .itemsCallback(new MaterialDialog.ListCallback() {

                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which) {
                                    // 二手
                                    case 0:
                                        LifeFragmentPublish.launch(getActivity());
                                        break;
                                    // 拍照
                                    case 1:
                                        WorkPublish.launch(getActivity());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
            }

        }.run();
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
        tabItems.add(new TabItem("1", "二手"));
        tabItems.add(new TabItem("2","兼职"));

        return tabItems;
    }

    @Override
    protected Fragment newFragment(TabItem bean) {
        // 热门评论
//        if ("0".equals(bean.getType())) {
//            return TimelineHotCommentFragment.newInstance(mStatusContent);
//        }
        // 二手
        if ("1".equals(bean.getType())) {
            return LifeDefFragment.newInstance("commodity");
        }
        // 兼职
        else if ("2".equals(bean.getType())) {
            return LifeDefFragment.newInstance("work");
        }else {
            return null;
        }

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
