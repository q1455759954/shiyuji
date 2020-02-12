package com.example.administrator.shiyuji.ui.fragment.timelineComments;


import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.support.action.DoLikeAction;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.activity.publish.PublishActivity;
import com.example.administrator.shiyuji.ui.fragment.publish.EmotionFragment;
import com.example.administrator.shiyuji.ui.fragment.publish.comment.CustomDialog;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineItemView;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.TabItem;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;
import com.example.administrator.shiyuji.ui.widget.TimelineDetailScrollView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 微博详情页
 *
 */
public class TimelineDetailPagerFragment extends ATabsTabLayoutFragment<TabItem>
        implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener,SwipeRefreshLayout.OnRefreshListener, DoLikeAction.OnLikeCallback {

    private static final int HOT_CMT_SHOW_MIN_COUNT = 20;

    public static void launch(Activity from, StatusContent status) {
        FragmentArgs args = new FragmentArgs();

        args.add("status", status);

        SinaCommonActivity.launch(from, TimelineDetailPagerFragment.class, args);
    }

    public static final String ACTION_REFRESH_CMT_REPLY = "org.aisen.weibo.sina.ACTION_REFRESH_CMT_REPLY";
    public static final String ACTION_REFRESH_CMT_CREATE = "org.aisen.weibo.sina.ACTION_REFRESH_CMT_CREATE";
    public static final String ACTION_REFRESH_REPOST = "org.aisen.weibo.sina.ACTION_REFRESH_ACTION_REFRESH_REPOST";

    @ViewInject(id = R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewInject(id = R.id.layHeader)
    RelativeLayout layHeader;
    @ViewInject(id = R.id.appbar)
    AppBarLayout appBarLayout;
    @ViewInject(id = R.id.toolbar)
    Toolbar toolbar;
    @ViewInject(id = R.id.layHeaderDivider)
    View layHeaderDivider;
    @ViewInject(id = R.id.txtAttitudes)
    TextView txtAttitudes;
    @ViewInject(id = R.id.overlay)
    View overlay;
    @ViewInject(id = R.id.laySroll)
    TimelineDetailScrollView laySroll;

    @ViewInject(id = R.id.btnCmt)
    View btnComment;
    @ViewInject(id = R.id.btnLike)
    View btnLike;
    @ViewInject(id = R.id.imgLike)
    ImageView imgLike;

    private StatusContent mStatusContent;

    private BizFragment bizFragment;

    @Override
    public int inflateContentView() {
        return -1;
    }

    @Override
    public int inflateActivityContentView() {
        return R.layout.ui_timeline_detail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectUtility.initInjectedView(getActivity(), this, ((BaseActivity) getActivity()).getRootView());
        layoutInit(inflater, savedInstanceState);

        // 添加HeaderView
        View itemConvertView = inflater.inflate(CommentHeaderItemView.COMMENT_HEADER_01_RES, layHeader, false);
        CommentHeaderItemView headerItemView = new CommentHeaderItemView(this, itemConvertView, mStatusContent);
//        headerItemView.onBindData(layHeader, null, 0);
        layHeader.addView(itemConvertView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

//        BizFragment.createBizFragment(getActivity()).createFabAnimator(action_menu);
        btnComment.setOnClickListener(this);
        setLikeView();
        return null;
    }

    public void setLikeView() {
        LikeBean likeBean = DoLikeAction.likeCache.get(mStatusContent.getRow_key());
        if (likeBean==null){
            likeBean= LikeDB.get(mStatusContent.getRow_key());
        }
        if (btnLike != null) {
            btnLike.setTag(mStatusContent);
            btnLike.setOnClickListener(this);

            if (likeBean != null && likeBean.isLiked()) {
                imgLike.setSelected(true);

            }else if (likeBean==null && mStatusContent.getFavorited()){
                imgLike.setSelected(mStatusContent.getFavorited());

            } else {
                imgLike.setSelected(false);
            }
        }
    }

    @Override
    public TabLayout getTablayout() {
        return (TabLayout) getActivity().findViewById(R.id.tabLayout);
    }

    @Override
    public ViewPager getViewPager() {
        return (ViewPager) getActivity().findViewById(R.id.viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStatusContent = savedInstanceState != null ? (StatusContent) savedInstanceState.getSerializable("status")
                : (StatusContent) getArguments().getSerializable("status");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bizFragment = BizFragment.createBizFragment(this);

        BaseActivity activity = (BaseActivity) getActivity();

        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("微博详情");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);
    }

    @Override
    protected void setupTabLayout(Bundle savedInstanceSate) {
        super.setupTabLayout(savedInstanceSate);

        TabLayout tabLayout = getTablayout();
        tabLayout.setPadding(Utils.dip2px(getActivity(), 8), tabLayout.getPaddingTop(), tabLayout.getPaddingRight(), tabLayout.getPaddingBottom());
        tabLayout.setTabTextColors(getResources().getColor(R.color.text_54),
                getResources().getColor(R.color.text_80));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("status", mStatusContent);
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
        super.layoutInit(inflater, savedInstanceState);

        appBarLayout.addOnOffsetChangedListener(this);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

//        for (int i = 0; i < action_menu.getChildCount(); i++) {
//            if (action_menu.getChildAt(i) instanceof AddFloatingActionButton) {
//                action_menu.getChildAt(i).setOnClickListener(this);
//
//                break;
//            }
//        }

        mHandler.postDelayed(initCurrentFragment, 100);
    }

    @Override
    public void onResume() {
        super.onResume();

//        UMengUtil.onPageStart(getActivity(), "微博评论页");

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_CMT_CREATE);
        filter.addAction(ACTION_REFRESH_CMT_REPLY);
        filter.addAction(ACTION_REFRESH_REPOST);
        getActivity().registerReceiver(receiver, filter);

//        setLikeText();
    }

    @Override
    public void onPause() {
        super.onPause();

//        UMengUtil.onPageEnd(getActivity(), "微博评论页");

        getActivity().unregisterReceiver(receiver);
    }


    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        if (getCurrentFragment() != null && getCurrentFragment() instanceof APagingFragment &&
                ((APagingFragment) getCurrentFragment()).getRefreshView() != null) {
            laySroll.setRefreshView(((APagingFragment) getCurrentFragment()).getRefreshView());
        }

        // 切换了Page就显示Fab
//        BizFragment.createBizFragment(getActivity()).getFabAnimator().show();
    }

    private Handler mHandler = new Handler();

    Runnable initCurrentFragment = new Runnable() {

        @Override
        public void run() {
            if (getCurrentFragment() != null && getCurrentFragment() instanceof APagingFragment &&
                    ((APagingFragment) getCurrentFragment()).getRefreshView() != null) {
                laySroll.setRefreshView(((APagingFragment) getCurrentFragment()).getRefreshView());
            }
            else {
                mHandler.postDelayed(initCurrentFragment, 100);
            }
        }

    };

    @Override
    protected ArrayList<TabItem> generateTabs() {

        ArrayList<TabItem> tabItems = new ArrayList<>();

            tabItems.add(new TabItem("1", String.format(getString(R.string.comment_format), String.valueOf(mStatusContent.getComments_count()))));
            tabItems.add(new TabItem("2", String.format(getString(R.string.attitudes_format),String.valueOf(mStatusContent.getAttitudes_count()))));

        return tabItems;
    }

    @Override
    protected Fragment newFragment(TabItem bean) {

        // 微博评论
         if ("1".equals(bean.getType())) {
            return TimelineCommentFragment.newInstance(mStatusContent);
        }
        // 微博点赞用户
        else if ("2".equals(bean.getType())) {
            return TimelineRepostFragment.newInstance(mStatusContent);
        }

        return null;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int visibility = View.VISIBLE;
        // 如果是AppbarLayout滑动到了最顶端，要把这个divider隐藏掉
        if (getTablayout().getHeight() + toolbar.getHeight() - appBarLayout.getHeight() == verticalOffset) {
            visibility = View.GONE;
        }
        if (layHeaderDivider.getVisibility() != visibility)
            layHeaderDivider.setVisibility(visibility);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_cmts, menu);
        menu.removeItem(R.id.fav);
        menu.removeItem(R.id.repost);
        menu.removeItem(R.id.comment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnCmt:
                CustomDialog mCustomDialog = new CustomDialog(getActivity(),this, R.style.customdialogstyle,mStatusContent.getRow_key(),"",null,"comment");
                mCustomDialog.setOnKeyListener(keylistener);
                mCustomDialog.setCancelable(true);
                mCustomDialog.show();
//
//                TimelineDetailPagerFragment.launch(getActivity(),mStatusContent );
                break;
            case R.id.btnLike:
                StatusContent s = (StatusContent) v.getTag();

                LikeBean likeBean = DoLikeAction.likeCache.get(s.getRow_key());
                if (likeBean==null){
                    likeBean=LikeDB.get(s.getRow_key());
                }
                boolean like = likeBean == null? !s.getFavorited():!likeBean.isLiked();

                v.findViewById(R.id.imgLike).setSelected(like);

                bizFragment.doLike(s, like, v, this);
                break;
            case R.id.btnRepost:
                String re_txt = "转发"+"<a href='/n/BanneyZ'>"+"@"+mStatusContent.getUserInfo().getNickname()+"</a>的微博";
                PublishBean bean = new PublishBean();
                bean.setRe_txt(re_txt);
                bean.setRe_rowKey(mStatusContent.getRow_key());

                PublishActivity.publishStatus(getActivity(),bean);
                break;
        }
    }


    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            Log.i("TAG", "键盘code---" + keyCode);
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                return false;
            } else if(keyCode == KeyEvent.KEYCODE_DEL){//删除键
//                if(dialog != null){
//                    mCustomDialog.del();
//                }
                return false;
            }else{
                return true;

            }
        }
    };

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

    public void refreshEnd() {
        swipeRefreshLayout.setRefreshing(false);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent != null ? intent.getAction() : "";

            if (ACTION_REFRESH_CMT_CREATE.equals(action) || ACTION_REFRESH_CMT_REPLY.equals(action)) {
                swipeRefreshLayout.setRefreshing(true);

                onRefresh((APagingFragment) getFragment(0));
            }
            else if (ACTION_REFRESH_REPOST.equals(action)) {
                swipeRefreshLayout.setRefreshing(true);

                onRefresh((APagingFragment) getFragment(1));
            }
        }

    };

    @Override
    public void onLikeFaild(BizFragment.ILikeBean data) {
        setLikeView();
    }

    @Override
    public void onLikeSuccess(BizFragment.ILikeBean data, View likeView) {
        if (getActivity() == null)
            return;

        setLikeView();

        if (likeView.getTag() == data) {
            bizFragment.animScale(likeView);
        }
    }
}
