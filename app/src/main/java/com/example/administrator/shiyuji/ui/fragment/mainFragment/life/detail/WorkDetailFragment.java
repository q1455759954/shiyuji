package com.example.administrator.shiyuji.ui.fragment.mainFragment.life.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.administrator.shiyuji.support.action.TurnAction;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.activity.tuikit.ChatActivity;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.WorkInfo;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.detail.AppointmentDetailFragment;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2019/12/8.
 */

public class WorkDetailFragment extends ABaseFragment implements View.OnClickListener,TurnAction.TurnCallback{


    public static void launch(Activity from, LifeInfo lifeInfo) {
        FragmentArgs args = new FragmentArgs();

        args.add("workInfo", lifeInfo.getWorkInfo());

        SinaCommonActivity.launch(from, WorkDetailFragment.class, args);
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

    private WorkInfo workInfo;
    Calendar calendar = Calendar.getInstance();
    private Bitmap bitmap;
    private boolean ifChangedAvatar = false;

    @Override
    public int inflateContentView() {
        return -1;
    }

    @Override
    public int inflateActivityContentView() {
        return R.layout.fragment_life_detail_work;
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
        event_title.setText(workInfo.getTitle());

        content.setText(workInfo.getContent());
        // desc时间
        String from = workInfo.getCreate_at();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(Long.parseLong(from));
        String dateStr = dateformat.format(gc.getTime());
        tv_releasetime.setText(dateStr);

        BitmapLoader.getInstance().display(this, AisenUtils.getUserPhoto(workInfo.getUserInfo()), iv_onlinehead, ImageConfigUtils.getLargePhotoConfig());

        tv_originator.setText(workInfo.getUserInfo().getNickname());

        iv_onlinehead.setOnClickListener(userInfoListener);
        release_add.setOnClickListener(listener);

        if (workInfo.getUserInfo().getId().equals(AppContext.getAccount().getUserInfo().getId())) {
            //如果该信息是用户发布的，显示下架按钮
            if (workInfo.isState()) {
                //已经下架
                turn.setText("已下架");
                turn.setBackgroundColor(this.getResources().getColor(R.color.gray));
                turn.setClickable(false);
                turn.setVisibility(View.VISIBLE);
            } else {
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
                params.addParameter("row_key",workInfo.getRow_key());
                params.addParameter("user", String.valueOf(workInfo.getUserInfo().getId()));
                params.addParameter("type","work");
                TurnAction action = new TurnAction(getActivity(),params,WorkDetailFragment.this);
                action.run();

            }
        };
    View.OnClickListener userInfoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), UserActivity.class);
            intent.putExtra("userInfo",workInfo.getUserInfo());
            getActivity().startActivity(intent);
        }
    };

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
        chatInfo.setId(workInfo.getUserInfo().getIdentifier());
        chatInfo.setChatName(workInfo.getUserInfo().getNickname());
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chatInfo", chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        workInfo = savedInstanceState != null ? (WorkInfo) savedInstanceState.getSerializable("workInfo")
                : (WorkInfo) getArguments().getSerializable("workInfo");
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

        outState.putSerializable("workInfo", workInfo);
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
    public void onSuccess() {
        workInfo.setState(true);
        turn.setText("已下架");
        turn.setBackgroundColor(this.getResources().getColor(R.color.gray));
        turn.setClickable(false);
        turn.setVisibility(View.VISIBLE);
    }
}
