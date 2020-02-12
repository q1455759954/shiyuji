package com.example.administrator.shiyuji.ui.fragment.mainFragment.life.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.support.action.TurnAction;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.activity.tuikit.ChatActivity;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.detail.AppointmentDetailFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.LooperPagerAdapter;
import com.example.administrator.shiyuji.ui.fragment.tuikit.util.TUikitUtil;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.LoopViewPager;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Administrator on 2019/10/18.
 */

public class LifeDetailFragment extends ABaseFragment implements View.OnClickListener, LoopViewPager.OnViewPagerTouchListener, ViewPager.OnPageChangeListener, TurnAction.TurnCallback {


    public static void launch(Activity from, LifeInfo lifeInfo) {
        FragmentArgs args = new FragmentArgs();

        args.add("lifeInfo", lifeInfo);

        SinaCommonActivity.launch(from, LifeDetailFragment.class, args);
        AisenUtils.changeOpenActivityStyle(from);

    }

    private boolean isShoucang = false;
    private boolean mIsTouch = false;//触摸判断
    private Handler mHandler;
    private LooperPagerAdapter mLooperPagerAdapter;
    private static List<String> sPics = new ArrayList<>();

    @ViewInject(id = R.id.ic_back)
    ImageView ic_back;
    @ViewInject(id = R.id.txt_title)
    TextView txt_title;
    @ViewInject(id = R.id.txt_content)
    TextView txt_content;
    @ViewInject(id = R.id.txt_userName)
    TextView txt_userName;
    @ViewInject(id = R.id.txt_price)
    TextView txt_price;
    @ViewInject(id = R.id.onlineTime)
    TextView onlineTime;
    @ViewInject(id = R.id.looper_pager)
    LoopViewPager mLoopPager;
    @ViewInject(id = R.id.looper_point)
    LinearLayout mPointContainer;
    @ViewInject(id = R.id.linearLayout_)
    LinearLayout linearLayout_;

    @ViewInject(id = R.id.turn)
    TextView turn;

    private LifeInfo lifeInfo;
    Calendar calendar = Calendar.getInstance();
    private Bitmap bitmap;
    private boolean ifChangedAvatar = false;

    @Override
    public int inflateContentView() {
        return -1;
    }

    @Override
    public int inflateActivityContentView() {
        return R.layout.fragment_life_detail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectUtility.initInjectedView(getActivity(), this, ((BaseActivity) getActivity()).getRootView());
        layoutInit(inflater, savedInstanceState);
        //照片url
        sPics.clear();
        for (PicUrls picUrls :lifeInfo.getCommodity().getPicUrls()){
            sPics.add(picUrls.getThumbnail_pic());
        }
        initView();

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        txt_title.setText(lifeInfo.getCommodity().getTitle());
        txt_content.setText(lifeInfo.getCommodity().getContent());
        txt_price.setText(lifeInfo.getCommodity().getPrice());
        txt_userName.setText(lifeInfo.getCommodity().getUserInfo().getNickname());
        onlineTime.setText(lifeInfo.getCommodity().getOnLineTime());

        if (lifeInfo.getCommodity().isState()){
            //已经下架
            turn.setText("已下架");
            linearLayout_.setBackgroundColor(this.getResources().getColor(R.color.gray));
            turn.setClickable(false);
        }else {
            //如果是用户自己的
            if (lifeInfo.getCommodity().getUserInfo().getId().equals(AppContext.getAccount().getUserInfo().getId())){
                turn.setText("下架");
                turn.setClickable(true);
                linearLayout_.setOnClickListener(turnListener);
            }else {
                //显示咨询按钮
                linearLayout_.setOnClickListener(listener);
            }
        }

        return null;
    }

    /**
     * 咨询，创建单聊
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startChatActivity();
        }
    };


    private void startChatActivity() {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(TIMConversationType.Group);
        chatInfo.setId(lifeInfo.getCommodity().getUserInfo().getIdentifier());
        chatInfo.setChatName(lifeInfo.getCommodity().getUserInfo().getNickname());
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chatInfo", chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 下架监听器
     */
    View.OnClickListener turnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Params params = new Params();
            params.addParameter("row_key",lifeInfo.getCommodity().getRow_key());
            params.addParameter("user", String.valueOf(lifeInfo.getCommodity().getUserInfo().getId()));
            params.addParameter("type","commodity");
            TurnAction action = new TurnAction(getActivity(),params,LifeDetailFragment.this);
            action.run();
        }
    };
    public boolean canDisplay() {
        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler= new Handler();
        lifeInfo = savedInstanceState != null ? (LifeInfo) savedInstanceState.getSerializable("lifeInfo")
                : (LifeInfo) getArguments().getSerializable("lifeInfo");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //绑定到窗口时
        mHandler.post(mLooperTask);
//        BaseActivity activity = (BaseActivity) getActivity();
//        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("活动详情");
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("lifeInfo", lifeInfo);
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
        super.layoutInit(inflater, savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mLooperTask);
    }

    private Runnable mLooperTask = new Runnable() {
        @Override
        public void run() {
            if(!mIsTouch){
                //切换viewPager到下一个
                int currentItem = mLoopPager.getCurrentItem();
                mLoopPager.setCurrentItem(++currentItem,true);
            }
            mHandler.postDelayed(this,3000);
        }
    };
    private void initView(){
        //为什么设置适配器
        mLooperPagerAdapter = new LooperPagerAdapter();
        mLooperPagerAdapter.setData(sPics,getActivity());
        mLoopPager.setAdapter(mLooperPagerAdapter);
        mLoopPager.setOnViewPagerTouchListener(this);
        mLoopPager.addOnPageChangeListener(this);
        //加点
        insertPoint();
        mLoopPager.setCurrentItem(mLooperPagerAdapter.getDataRealSize()*100,false);
    }
    private void insertPoint(){
        for (int i=0;i<sPics.size();i++){
            View point = new View(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40,40);
            layoutParams.leftMargin = 20;
            point.setBackground(getResources().getDrawable(R.drawable.shape_looper_normal_point));
            point.setLayoutParams(layoutParams);
            mPointContainer.addView(point);
        }
    }
    @Override
    public void onPagerTouch(boolean isTouch) {
        mIsTouch = isTouch;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //viewPager停下来以后选中的位置，要改变他到真证的位置
        int realPosition;
        if(mLooperPagerAdapter.getDataRealSize()!=0){
            realPosition = position % mLooperPagerAdapter.getDataRealSize();
        }else {
            realPosition = 0;
        }
        setSelectPoint(realPosition);
    }

    private void setSelectPoint(int realPosition) {
        for(int i=0;i<mPointContainer.getChildCount();i++){
            View point = mPointContainer.getChildAt(i);
            if(i!=realPosition){
                point.setBackgroundResource(R.drawable.shape_looper_normal_point);
            }else {
                point.setBackgroundResource(R.drawable.shape_looper_selected_point);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSuccess() {
        lifeInfo.getCommodity().setState(true);
        turn.setText("已下架");
        linearLayout_.setBackgroundColor(this.getResources().getColor(R.color.gray));
        turn.setClickable(false);
        turn.setVisibility(View.VISIBLE);
    }
}


