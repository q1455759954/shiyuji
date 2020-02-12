package com.example.administrator.shiyuji.ui.activity.user;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.administrator.shiyuji.MainActivity;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.support.action.DoAttendAction;
import com.example.administrator.shiyuji.support.action.DoLikeAction;
import com.example.administrator.shiyuji.support.bean.AttendBean;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.sqlit.AttendDB;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.tuikit.ChatActivity;
import com.example.administrator.shiyuji.ui.activity.tuikit.ConversationActivity;
import com.example.administrator.shiyuji.ui.fragment.adapter.FragmentPagerAdapter;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.base.ARecycleViewSwipeRefreshFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.AppointmentFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item.AppointmentDefFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.item.LifeDefFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.mine.MineFragment;
import com.example.administrator.shiyuji.ui.fragment.picture.PictureFragment;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineDefFragment;
import com.example.administrator.shiyuji.ui.fragment.user.ChangeUserInfoFragment;
import com.example.administrator.shiyuji.ui.fragment.user.UserInfoFragment;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;
import com.example.administrator.shiyuji.util.widgetutil.WidgetUtil;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;

import java.io.Serializable;

import static android.R.attr.data;
import static com.example.administrator.shiyuji.R.id.btnLike;
import static com.example.administrator.shiyuji.R.id.imgLike;
import static com.example.administrator.shiyuji.R.id.user_img;

/**
 * Created by Administrator on 2019/9/11.
 */

public class UserActivity extends BaseActivity implements ViewPager.OnPageChangeListener,DoAttendAction.OnAttendCallback,Serializable{

    /**
     * ScrollView上半部分
     */
    @ViewInject(id = R.id.toolbar)
    Toolbar mToolbar;
    /**
     * 头像
     */
    @ViewInject(id = R.id.head_iv)
    ImageView headIv;
    /**
     * CollapsingToolbarLayout内部显示内容部分
     */
    @ViewInject(id = R.id.head_layout)
    LinearLayout headLayout;
    /**
     * tab分类条目
     */
    @ViewInject(id = R.id.toolbar_tab)
    TabLayout toolbarTab;

    @ViewInject(id = R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    /**
     * 折叠部分
     */
    @ViewInject(id = R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    /**
     * ViewPager
     */
    @ViewInject(id = R.id.main_vp_container)
    ViewPager mViewPager;
    /**
     * ScrollView
     */
    @ViewInject(id = R.id.nsv)
    NestedScrollView nsv;
    /**
     * 整个布局
     */
    @ViewInject(id = R.id.coordinator_Layout)
    CoordinatorLayout coordinatorLayout;

    /**
     * 关注或者编辑
     */
    @ViewInject(id = R.id.editOrAttention)
    Button editOrAttention;
    /**
     * 聊天
     */
    @ViewInject(id = R.id.chat)
    ImageView chat;
    @ViewInject(id = R.id.label)
    TextView label;
    @ViewInject(id = R.id.name)
    TextView name;
    @ViewInject(id = R.id.gender)
    CircleImageView gender;
    @ViewInject(id = R.id.attends)
    TextView attends;
    @ViewInject(id = R.id.fans)
    TextView fans;


    public static final String CHANGE_USER_INFO = "com.shiyuji.CHANGE_USER_INFO";

    private String[] mBean = {"动态","约吗","二手","兼职","简介"};

    private UserInfo userInfo;
    private MyViewPagerAdapter myViewPagerAdapter;
    private boolean flag = false;//判断是否是用户本人
//    private ViewPagerAdapter myPagerAdapter;
    private BizFragment bizFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_mine_main_page);
        Bundle bundle = getIntent().getExtras();
        userInfo = (UserInfo) bundle.getSerializable("userInfo");
        flag = bundle.getSerializable("flag")==null?false: (boolean) bundle.getSerializable("flag");
        bizFragment = BizFragment.createBizFragment(this);

        if (flag){
            editOrAttention.setText("编辑");
            editOrAttention.setOnClickListener(listener);
        }else {
            setAttendView();
            editOrAttention.setOnClickListener(attendListener);
        }
        //跳转到聊天界面
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatActivity();
            }
        });

        //用toolBar替换ActionBar
        setToolBarReplaceActionBar();

        //把title设置到CollapsingToolbarLayout上
        setTitleToCollapsingToolbarLayout();

        // 给viewpager设置适配器
//        setViewPagerAdapter();

        toolbarTab.setupWithViewPager(mViewPager);
        myViewPagerAdapter = new MyViewPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(myViewPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(0);
        WidgetUtil.setIndicator(toolbarTab,20,20);//设置下划线长度

        label.setText(userInfo.getIntroduce());

        name.setText(userInfo.getNickname());
        gender.setImageResource(userInfo.getGender().equals("男")? R.drawable.xingbienan : R.drawable.xingbienv);
        //设置头像
        BitmapLoader.getInstance().display(this, AisenUtils.getUserPhoto(userInfo), headIv, ImageConfigUtils.getLargePhotoConfig());
        attends.setText(String.valueOf(userInfo.getAttends()));
        fans.setText(String.valueOf(userInfo.getFans()));
//        //设置是否关注
//        if (userInfo.isIfAttend()){
//            editOrAttention.setText("已关注");
//        }else {
//            editOrAttention.setText("关注");
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            changeInfo();
        }
    }

    /**
     * 修改完资料后回调
     */
    public void changeInfo(){
        UserInfo u = AppContext.getAccount().getUserInfo();
        label.setText(u.getIntroduce());

        name.setText(u.getNickname());
        gender.setImageResource(u.getGender().equals("男")? R.drawable.xingbienan : R.drawable.xingbienv);
        //设置头像
        BitmapLoader.getInstance().display(this, AisenUtils.getUserPhoto(u), headIv, ImageConfigUtils.getLargePhotoConfig());
        attends.setText(String.valueOf(u.getAttends()));
        fans.setText(String.valueOf(u.getFans()));

    }

    /**
     * 设置关注信息
     */
    public void setAttendView() {
        AttendBean attendBean = DoAttendAction.attendCache.get(String.valueOf(userInfo.getId()));
        if (attendBean==null){
            attendBean= AttendDB.get(String.valueOf(userInfo.getId()));
        }
        if (editOrAttention != null) {
            editOrAttention.setTag(userInfo);
            editOrAttention.setOnClickListener(attendListener);

            if (attendBean != null && attendBean.isAttend()) {
                editOrAttention.setText("已关注");

            }else if (attendBean==null && userInfo.isIfAttend()){
                editOrAttention.setText("已关注");

            } else {
                editOrAttention.setText("关注");

            }
        }
    }

    /**
     * 跳转到私信界面
     */
    private void startChatActivity() {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(TIMConversationType.C2C);
        chatInfo.setId(userInfo.getIdentifier());
        chatInfo.setChatName(userInfo.getNickname());
        Intent intent = new Intent(UserActivity.this, ChatActivity.class);
        intent.putExtra("chatInfo", chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    /**
     * 跳转到编辑界面的监听器
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ChangeUserInfoFragment.launch(UserActivity.this, userInfo);
        }
    };

    /**
     * 关注按钮
     */
    View.OnClickListener attendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserInfo u = (UserInfo) v.getTag();

            AttendBean attendBean = DoAttendAction.attendCache.get(String.valueOf(u.getId()));
            if (attendBean==null){
                attendBean=AttendDB.get(String.valueOf(u.getId()));
            }
            boolean attend = attendBean == null? !u.isIfAttend():!attendBean.isAttend();

//            v.findViewById(R.id.imgLike).setSelected(like);

            bizFragment.doAttend(u, attend, v, UserActivity.this);


//            new MakeAttends().execute();
        }
    };

    /**
     * 用toolBar替换ActionBar
     */
    private void setToolBarReplaceActionBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，
     * 设置到Toolbar上则不会显示
     */
    private void setTitleToCollapsingToolbarLayout() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -headLayout.getHeight() / 2) {
                    collapsingToolbarLayout.setTitle(userInfo.getNickname());
                    //使用下面两个CollapsingToolbarLayout的方法设置展开透明->折叠时你想要的颜色
                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
                    collapsingToolbarLayout.setContentScrimResource(R.color.black);
                    label.setVisibility(View.GONE);
                } else {
                    collapsingToolbarLayout.setContentScrimResource(R.drawable.bg_banner_dialog);
                    label.setVisibility(View.VISIBLE);
                    collapsingToolbarLayout.setTitle("");
                }
            }
        });
    }




    //在ActionBar设置条目
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        String msg = "";
//        switch (item.getItemId()) {
//            case R.id.webview:
//                msg += "博客跳转";
//                break;
//            case R.id.weibo:
//                msg += "微博跳转";
//                break;
//            case R.id.action_settings:
//                msg += "设置";
//                break;
//        }
//        if (!msg.equals("")) {
//            Toast.makeText(UserActivity.this, msg, Toast.LENGTH_SHORT).show();
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
    }

    private int size() {
        if (mBean!= null) {
            return mBean.length;
        }

        return mBean.length;
    }

    protected Fragment newFragment(int position) {
//        {"动态","约吗","二手","兼职","简介"};
        if (position==0){
            return TimelineDefFragment.newInstance("userTimeline",userInfo);
        }else if (position==1){
            return AppointmentDefFragment.newInstance("userAppointment",userInfo);
        }else if (position==2){
            return LifeDefFragment.newInstance("commodity",userInfo);
        }else if (position==3){
            return LifeDefFragment.newInstance("work",userInfo);
        }else if (position==4){
            return UserInfoFragment.newInstance();
        }
        return null;
    }

    @Override
    public void onAttendFaild(UserInfo data) {
        setAttendView();
    }

    @Override
    public void onAttendSuccess(UserInfo data, View view) {
        if (UserActivity.getRunningActivity() == null)
            return;

        setAttendView();

        if (view.getTag() == data) {
            bizFragment.animScale(view);
        }
    }


    class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = getFragmentManager().findFragmentByTag(makeFragmentName(position));
            if (fragment == null) {
                fragment = newFragment(position);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);

            Fragment fragment = getFragmentManager().findFragmentByTag(makeFragmentName(position));
            if (fragment != null)
                mCurTransaction.remove(fragment);
        }


        @Override
        public String getPageTitle(int position) {
            return mBean[position];
        }

        @Override
        protected String makeFragmentName(int position) {
            return mBean[position];
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        Fragment fragment =  myViewPagerAdapter.getItem(position);
//        if (fragment != null)
//            fragment.onTabRequestData();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

//    class MakeAttends extends WorkTask<Void, Void, String> {
//
//        @Override
//        public String workInBackground(Void... params) throws TaskException {
//            int uId = AppContext.getAccount().getUserInfo().getId();
//            int targetUser = userInfo.getId();
//            Params p = new Params();
//            p.addParameter("targetUser", String.valueOf(targetUser));
//            if (userInfo.isIfAttend()){
//                p.addParameter("type","cancel");
//            }else {
//                p.addParameter("type","attend");
//            }
//            return  SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).makeAttends(p);
//        }
//
//        @Override
//        protected void onSuccess(String result) {
//            super.onSuccess(result);
//            if (result.equals("ok")){
//                attendResult();
//            }
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CHANGE_USER_INFO);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * 发布完成后发布广播，这里接受广播进行更新
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent != null ? intent.getAction() : "";

            if (CHANGE_USER_INFO.equals(action) ) {
                changeInfo();

            }

        }

    };

}