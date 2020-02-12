package com.example.administrator.shiyuji.ui.fragment.search;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.activity.publish.PublishActivity;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.bean.PictureSize;
import com.example.administrator.shiyuji.ui.fragment.bean.SearchInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.TabItem;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item.AppointmentDefFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.home.HomeFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.home.HotFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.item.LifeDefFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.like.LikeDefFragment;
import com.example.administrator.shiyuji.ui.fragment.picture.PictureFragment;
import com.example.administrator.shiyuji.ui.fragment.search.user.AUserFragment;
import com.example.administrator.shiyuji.ui.fragment.search.user.UserDefFragment;
import com.example.administrator.shiyuji.ui.fragment.support.IPagingAdapter;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineDefFragment;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.ATabsTabLayoutFragment;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineDetailPagerFragment;
import com.example.administrator.shiyuji.ui.widget.AspectImageView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.Utils;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import scut.carson_ho.searchview.ICallBack;
import scut.carson_ho.searchview.SearchListView;
import scut.carson_ho.searchview.SearchView;
import scut.carson_ho.searchview.bCallBack;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * Created by Administrator on 2019/12/6.
 */

public class SearchFragment extends ATabsTabLayoutFragment<TabItem>
        implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {

    @ViewInject(id= R.id.tabLayout)
    TabLayout tabLayout;
    @ViewInject(id=R.id.viewPager)
    ViewPager viewPager;
    @ViewInject(id=R.id.search_view)
    SearchView searchView;

    private SearchInfo searchInfo;
    private SearchListView searchListView;
    private EditText et_search; // 搜索按键
    private TextView tv_clear;  // 删除搜索记录按键
    private String search = "";

    public static SearchFragment newInstance(SearchInfo searchInfo) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putSerializable("searchInfo",searchInfo);
        fragment.setArguments(args);

        return fragment;
    }
    public static void launch(Activity from, SearchInfo searchInfo, String search) {
        FragmentArgs args = new FragmentArgs();

        args.add("searchInfo", searchInfo);
        args.add("search", search);
        SinaCommonActivity.launch(from, SearchFragment.class, args);
    }
    public int inflateContentView() {
        return R.layout.fragment_search;
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
        searchInfo = savedInstanceState == null ? (SearchInfo) getArguments().getSerializable("searchInfo") :(SearchInfo) savedInstanceState.getSerializable("searchInfo");
        search = savedInstanceState == null ? (String) getArguments().getSerializable("search") :(String) savedInstanceState.getSerializable("search");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("searchInfo", searchInfo);
        outState.putSerializable("search", search);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        bizFragment = BizFragment.createBizFragment(this);


        try {
            Field fs = searchView.getClass().getDeclaredField("listView");
            fs.setAccessible(true);
            searchListView = (SearchListView) fs.get(searchView);
            searchListView.setVisibility(View.GONE);

            Field clear = searchView.getClass().getDeclaredField("tv_clear");
            clear.setAccessible(true);
            tv_clear = (TextView) clear.get(searchView);
            tv_clear.setVisibility(View.GONE);

            Field edit = searchView.getClass().getDeclaredField("et_search");
            edit.setAccessible(true);
            et_search = (EditText) edit.get(searchView);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //监听搜索框，获取焦点时显示搜索内容
        et_search.setOnFocusChangeListener(listener);
        searchListView.setOnItemClickListener(listListener);

        // 4. 设置点击键盘上的搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                new GetSearchInfo(AppContext.getAccount().getUserInfo().getId(),string).execute();
            }
        });


        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                getActivity().finish();
            }
        });

        et_search.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                et_search.getWindowVisibleDisplayFrame(r);
                int screenHeight = et_search.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom);
                if (heightDifference > 200) {
                    //软键盘显示
                    et_search.requestFocus();
                } else {
                    //软键盘隐藏
                    et_search.clearFocus();
                }
            }
        });


        //设置tablayout位置居中
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        BaseActivity activity = (BaseActivity) getActivity();
        activity.getSupportActionBar().setTitle("");// R.string.timeline_detail
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.hideToolbar();

        setHasOptionsMenu(true);
    }

    AdapterView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // 获取用户点击列表里的文字,并自动填充到搜索框内
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            String name = textView.getText().toString();
            et_search.setText(name);
            et_search.setSelection(name.length());
            et_search.clearFocus();
            new GetSearchInfo(AppContext.getAccount().getUserInfo().getId(),name).execute();
        }
    };

    View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                // 此处为得到焦点时的处理内容
                searchListView.setVisibility(View.VISIBLE);
                tv_clear.setVisibility(View.VISIBLE);
            } else {
                // 此处为失去焦点时的处理内容
                searchListView.setVisibility(View.GONE);
                tv_clear.setVisibility(View.GONE);
            }
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
        tabItems.add(new TabItem("1", "动态"));
        tabItems.add(new TabItem("2","用户"));
        tabItems.add(new TabItem("3","约"));
        tabItems.add(new TabItem("4","二手"));
        tabItems.add(new TabItem("5","兼职"));
        return tabItems;
    }

    @Override
    protected Fragment newFragment(TabItem bean) {

        //动态
        if ("1".equals(bean.getType())) {
            return TimelineDefFragment.newInstance("a",searchInfo);
        }
        // 用户
        else if ("2".equals(bean.getType())) {
            return UserDefFragment.newInstance("a",searchInfo);
        }
        // 约
        else if ("3".equals(bean.getType())) {
            return AppointmentDefFragment.newInstance("appointment",searchInfo);
        }
        // 二手
        else if ("4".equals(bean.getType())) {
            return LifeDefFragment.newInstance("commodity",searchInfo);
        }
        //兼职
        else if ("5".equals(bean.getType())){
            return LifeDefFragment.newInstance("work",searchInfo);
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


    class GetSearchInfo extends WorkTask<Void, Void, SearchInfo> {

        int uId;
        String text;

        public GetSearchInfo(int uId, String text) {
            super();
            this.uId = uId;
            this.text = text;
        }

        @Override
        public SearchInfo workInBackground(Void... params) throws TaskException {

            SearchInfo searchInfo = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).getSearchInfo(text);

            return searchInfo;
        }

        @Override
        protected void onSuccess(SearchInfo searchInfo) {
            super.onSuccess(searchInfo);
            Setting action = getSetting("base_url");
            String base_url = action.getValue().split("/")[2].split(":")[0];
            for (UserInfo userInfo:searchInfo.getUserInfoList()){
                userInfo.setAvatar(userInfo.getAvatar().replaceAll("localhost",base_url));
            }
            for (StatusContent statusContent:searchInfo.getContentList()){
                if (statusContent.getPic_urls()!=null){
                    for (PicUrls picUrls : statusContent.getPic_urls()){
                        picUrls.setThumbnail_pic(picUrls.getThumbnail_pic().replaceAll("localhost",base_url));
                    }
                }
                if (statusContent.getVideoInfo()!=null){
                    statusContent.getVideoInfo().setPic_url(statusContent.getVideoInfo().getPic_url().replaceAll("localhost",base_url));
                    statusContent.getVideoInfo().setVideo_url(statusContent.getVideoInfo().getVideo_url().replaceAll("localhost",base_url));
                }
                statusContent.getUserInfo().setAvatar(statusContent.getUserInfo().getAvatar().replaceAll("localhost",base_url));
            }
            searchInfo.setQueryStr(text);

            SearchFragment.launch(getActivity(),searchInfo,et_search.getText().toString());
            getActivity().finish();

//            //动态
//            TimelineDefFragment timelineDefFragment = (TimelineDefFragment) getFragment(0);
//            if (timelineDefFragment!=null){
//                timelineDefFragment.setAdapterItems(new ArrayList<StatusContent>());
//                IPagingAdapter.Utils.addItemsAndRefresh(timelineDefFragment.getAdapter(), (List)searchInfo.getContentList());
//            }
//
//            //用户
//            UserDefFragment userDefFragment = (UserDefFragment) getFragment(1);
//            if (userDefFragment!=null){
//                userDefFragment.setAdapterItems(new ArrayList<UserInfo>());
//                IPagingAdapter.Utils.addItemsAndRefresh(userDefFragment.getAdapter(), (List)searchInfo.getUserInfoList());
//            }
//
//            //约
//            AppointmentDefFragment appointmentDefFragment = (AppointmentDefFragment) getFragment(2);
//            if (appointmentDefFragment!=null){
//                appointmentDefFragment.setAdapterItems(new ArrayList<AppointmentInfo>());
//                IPagingAdapter.Utils.addItemsAndRefresh(appointmentDefFragment.getAdapter(), (List)searchInfo.getAppointmentInfoList());
//            }
//
//            //二手
//            LifeDefFragment commidity = (LifeDefFragment) getFragment(3);
//            if (commidity!=null){
//                commidity.setAdapterItems(new ArrayList<LifeInfo>());
//                IPagingAdapter.Utils.addItemsAndRefresh(commidity.getAdapter(), (List)searchInfo.getLifeInfos());
//            }
//
//            //兼职
//            LifeDefFragment work = (LifeDefFragment) getFragment(4);
//            if (work!=null){
//                work.setAdapterItems(new ArrayList<LifeInfo>());
//                IPagingAdapter.Utils.addItemsAndRefresh(work.getAdapter(), (List)searchInfo.getLifeInfos());
//            }
//            SearchFragment.this.searchInfo = searchInfo;

        }
    }

}
