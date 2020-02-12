package com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.support.action.DeleteAction;
import com.example.administrator.shiyuji.support.action.TurnAction;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.activity.tuikit.ChatActivity;
import com.example.administrator.shiyuji.ui.activity.tuikit.ConversationActivity;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.tuikit.util.TUikitUtil;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2019/10/8.
 */

public class AppointmentDetailFragment extends ABaseFragment implements View.OnClickListener,TurnAction.TurnCallback{


    public static void launch(Activity from, AppointmentInfo appointmentInfo) {
        FragmentArgs args = new FragmentArgs();

        args.add("appointmentInfo", appointmentInfo);

        SinaCommonActivity.launch(from, AppointmentDetailFragment.class, args);
        AisenUtils.changeOpenActivityStyle(from);

    }

    @ViewInject(id = R.id.ic_back)
    ImageView ic_back;
    @ViewInject(id = R.id.event_title)
    TextView event_title;
    @ViewInject(id = R.id.iv_onlinehead)
    CircleImageView iv_onlinehead;
    @ViewInject(id = R.id.tv_originator)
    TextView tv_originator;
    @ViewInject(id = R.id.tv_releasetime)
    TextView tv_releasetime;
    @ViewInject(id = R.id.content)
    TextView content;
    @ViewInject(id = R.id.release_add)
    TextView release_add;
    @ViewInject(id = R.id.attend)
    TextView attend;
    //下架按钮
    @ViewInject(id = R.id.turn)
    Button turn;

    private AppointmentInfo appointmentInfo;
    Calendar calendar = Calendar.getInstance();
    private Bitmap bitmap;
    private boolean ifChangedAvatar = false;

    @Override
    public int inflateContentView() {
        return -1;
    }

    @Override
    public int inflateActivityContentView() {
        return R.layout.fragment_appointment_join;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectUtility.initInjectedView(getActivity(), this, ((BaseActivity) getActivity()).getRootView());
        layoutInit(inflater, savedInstanceState);
        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        event_title.setText(appointmentInfo.getTitle());

        content.setText(appointmentInfo.getContent());
        // desc时间
        String from = appointmentInfo.getCreate_at();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(Long.parseLong(from));
        String dateStr = dateformat.format(gc.getTime());
        tv_releasetime.setText(dateStr);

        BitmapLoader.getInstance().display(this, AisenUtils.getUserPhoto(appointmentInfo.getUserInfo()), iv_onlinehead, ImageConfigUtils.getLargePhotoConfig());

        tv_originator.setText(appointmentInfo.getUserInfo().getNickname());

        iv_onlinehead.setOnClickListener(userInfoListener);
        release_add.setOnClickListener(listener);

        if (appointmentInfo.getUserInfo().getId().equals(AppContext.getAccount().getUserInfo().getId())){
            //如果该信息是用户发布的，显示下架按钮
            if (appointmentInfo.isState()){
                //已经下架
                turn.setText("已下架");
                turn.setBackgroundColor(this.getResources().getColor(R.color.gray));
                turn.setClickable(false);
                turn.setVisibility(View.VISIBLE);
            }else {
                turn.setVisibility(View.VISIBLE);
                turn.setOnClickListener(turnListener);
            }
        }
        return null;
    }

    /**
     * 下架监听器
     */
    View.OnClickListener turnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Params params = new Params();
            params.addParameter("row_key",appointmentInfo.getRow_key());
            params.addParameter("user", String.valueOf(appointmentInfo.getUserInfo().getId()));
            params.addParameter("type","appointment");
            TurnAction action = new TurnAction(getActivity(),params,AppointmentDetailFragment.this);
            action.run();

        }
    };


    View.OnClickListener userInfoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), UserActivity.class);
            intent.putExtra("userInfo",appointmentInfo.getUserInfo());
            getActivity().startActivity(intent);
        }
    };

    /**
     * 点击加入监听器
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TUikitUtil.getGroupMembers(appointmentInfo.getGroupId(), AppContext.getAccount().getUserInfo().getIdentifier(),AppointmentDetailFragment.this);
        }
    };

    /**
     * 查询完是否已加入群组后回调
     * @param groupId
     */
    public void applyJoinGroup(String groupId,boolean result){
        if (result){
            //已经加入直接跳转
            startChatActivity();
        }else if (!result){
            //没加入
            TUikitUtil.applyJoinGroup(appointmentInfo.getGroupId(),null,this);
        }else {
            //出错
            Toast.makeText(getActivity(),"加入失败，未知原因",Toast.LENGTH_SHORT);
        }
    }

    /**
     * 加入完毕，跳转
     * @param result
     */
    public void parseResult(boolean result){
        if (result){
            //加入成功
            startChatActivity();
        }else {
            Toast.makeText(getActivity(),"加入失败，未知原因",Toast.LENGTH_SHORT);
        }
    }

    private void startChatActivity() {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(TIMConversationType.Group);
        chatInfo.setId(appointmentInfo.getGroupId());
        chatInfo.setChatName("已加入群组");
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chatInfo", chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appointmentInfo = savedInstanceState != null ? (AppointmentInfo) savedInstanceState.getSerializable("appointmentInfo")
                : (AppointmentInfo) getArguments().getSerializable("appointmentInfo");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        BaseActivity activity = (BaseActivity) getActivity();
//        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("活动详情");
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("appointmentInfo", appointmentInfo);
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


    /**
     * 下架成功回调接口
     */
    @Override
    public void onSuccess() {
        appointmentInfo.setState(true);
        turn.setText("已下架");
        turn.setBackgroundColor(this.getResources().getColor(R.color.gray));
        turn.setClickable(false);
        turn.setVisibility(View.VISIBLE);
    }
}