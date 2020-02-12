package com.example.administrator.shiyuji.ui.fragment.search.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.support.action.DoAttendAction;
import com.example.administrator.shiyuji.support.action.DoLikeAction;
import com.example.administrator.shiyuji.support.bean.AttendBean;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.sqlit.AttendDB;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.publish.comment.CustomDialog;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.MyTextView;
import com.example.administrator.shiyuji.ui.widget.TimelinePicsView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Administrator on 2019/12/7.
 */

public class UserItemView extends ARecycleViewItemView<UserInfo> implements View.OnClickListener,DoAttendAction.OnAttendCallback {

    public static final int LAYOUT_RES = R.layout.item_user;


    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;

    @ViewInject(id = R.id.txtName)
    TextView txtName;

    @ViewInject(id = R.id.gender_img)
    ImageView gender;

    @ViewInject(id = R.id.txtDesc)
    TextView introduce;

    @ViewInject(id = R.id.attention_button)
    Button attention;


    private int textSize = 0;
    private static Map<String, String> groupMap;
    private int vPadding;

    private ABaseFragment fragment;
    private BizFragment bizFragment;

    private UserInfo data;

    public UserItemView(View convertView, ABaseFragment fragment) {
        super(fragment.getActivity(), convertView);

        this.fragment = fragment;
        bizFragment = BizFragment.createBizFragment(fragment);

        textSize = AppSettings.getTextSize();

    }


    @Override
    public void onBindData(View convertView, UserInfo data, int position) {
        this.data = data;

        txtName.setText(data.getNickname());
        introduce.setText(data.getIntroduce());
        gender.setImageResource(data.getGender().equals("男")? R.drawable.xingbienan : R.drawable.xingbienv);
        BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(data), imgPhoto, ImageConfigUtils.getLargePhotoConfig());

        setAttendView();


    }

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

            bizFragment.doAttend(u, attend, v, UserItemView.this);


//            new MakeAttends().execute();
        }
    };

    /**
     * 设置关注信息
     */
    public void setAttendView() {
        AttendBean attendBean = DoAttendAction.attendCache.get(String.valueOf(data.getId()));
        if (attendBean==null){
            attendBean= AttendDB.get(String.valueOf(data.getId()));
        }
        if (attention != null) {
            attention.setTag(data);
            attention.setOnClickListener(attendListener);

            if (attendBean != null && attendBean.isAttend()) {
                attention.setText("已关注");

            }else if (attendBean==null && data.isIfAttend()){
                attention.setText("已关注");

            } else {
                attention.setText("关注");

            }
        }
    }

    private void setUserInfo(final UserInfo user, TextView txtName, ImageView imgPhoto, ImageView imgVerified) {
        if (user != null) {
            txtName.setText(AisenUtils.getUserScreenName(user));

            if (imgPhoto != null) {
                BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(user), imgPhoto, ImageConfigUtils.getLargePhotoConfig());
            }

        }

    }

    public static void setTextSize(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }


    @Override
    public void onClick(View v) {

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
}

