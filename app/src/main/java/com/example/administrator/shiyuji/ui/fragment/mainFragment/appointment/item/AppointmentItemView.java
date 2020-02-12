package com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.support.action.DoLikeAction;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.detail.AppointmentDetailFragment;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineDetailPagerFragment;
import com.example.administrator.shiyuji.ui.fragment.tuikit.util.TUikitUtil;
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

/**
 * Created by Administrator on 2019/10/8.
 */

public class AppointmentItemView extends ARecycleViewItemView<AppointmentInfo> implements View.OnClickListener, DoLikeAction.OnLikeCallback {

    public static final int LAYOUT_RES = R.layout.item_appointment;

    @ViewInject(id = R.id.tv_acv_title)
    TextView tv_acv_title;

    @ViewInject(id = R.id.tv_acv_content)
    TextView tv_acv_content;
    @ViewInject(id = R.id.iv_onlinehead)
    CircleImageView iv_onlinehead;
    @ViewInject(id = R.id.tv_user)
    TextView tv_user;
    @ViewInject(id = R.id.info)
    TextView tv_info;
    @ViewInject(id = R.id.tv_releasetime)
    TextView tv_releasetime;



    private int textSize = 0;
    private static Map<String, String> groupMap;
    private int vPadding;

    private ABaseFragment fragment;
    private BizFragment bizFragment;

    private AppointmentInfo data;

    public AppointmentItemView(View convertView, ABaseFragment fragment) {
        super(fragment.getActivity(), convertView);

        this.fragment = fragment;
        bizFragment = BizFragment.createBizFragment(fragment);

        textSize = AppSettings.getTextSize();


    }

    /**
     * 获取群人数的回调方法
     * @param size
     */
    public void setAddInfo(int size) {
        String t = "已加入"+size+"人";
        tv_info.setText(t);
    }

    @Override
    public void onBindData(View convertView, AppointmentInfo data, int position) {
        this.data = data;
        tv_acv_title.setText(data.getTitle());
        tv_acv_content.setText(data.getContent());
        // desc时间
        String from = data.getCreate_at();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(Long.parseLong(from));
        String dateStr = dateformat.format(gc.getTime());
        tv_releasetime.setText(dateStr);



        BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(data.getUserInfo()), iv_onlinehead, ImageConfigUtils.getLargePhotoConfig());

        tv_user.setText(data.getUserInfo().getNickname());

        /**
         * 获取群人数
         */
        TUikitUtil.getGroupNum(data.getGroupId(), AppContext.getAccount().getUserInfo().getIdentifier(),AppointmentItemView.this);


    }

    /**
     * 设置点赞信息
     */
    public void setLikeView() {

    }



    public static void setTextSize(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onLikeFaild(BizFragment.ILikeBean data) {
        setLikeView();
    }

    @Override
    public void onLikeSuccess(BizFragment.ILikeBean data, View likeView) {
        if (fragment.getActivity() == null)
            return;

        setLikeView();

        if (likeView.getTag() == data) {
            bizFragment.animScale(likeView);
        }
    }

}
