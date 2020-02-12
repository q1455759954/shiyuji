package com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.like;

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
import com.example.administrator.shiyuji.ui.fragment.bean.NotificationInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineDetailPagerFragment;
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
 * Created by Administrator on 2019/9/27.
 */

public class LikeItemView extends ARecycleViewItemView<NotificationInfo> implements View.OnClickListener {

    public static final int LAYOUT_RES = R.layout.item_notification_ui;

    @ViewInject(id = R.id.cardView)
    CardView cardView;

    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;
    @ViewInject(id = R.id.txtName)
    TextView txtName;
    @ViewInject(id = R.id.txtDesc)
    TextView txtDesc;

    @ViewInject(id = R.id.imgPhoto_)
    ImageView imgPhoto_;
    @ViewInject(id = R.id.txtName_)
    TextView txtName_;
    @ViewInject(id = R.id.text_)
    MyTextView text_;

    @ViewInject(id = R.id.txtContent)
    MyTextView txtContent;
    /**
     * 回复按钮
     */
    @ViewInject(id = R.id.reply)
    TextView reply;



    private int textSize = 0;
    private static Map<String, String> groupMap;
    private int vPadding;

    private ABaseFragment fragment;
    private BizFragment bizFragment;

    private NotificationInfo data;

    public LikeItemView(View convertView, ABaseFragment fragment) {
        super(fragment.getActivity(), convertView);

        this.fragment = fragment;
        bizFragment = BizFragment.createBizFragment(fragment);

        textSize = AppSettings.getTextSize();

    }


    @Override
    public void onBindData(View convertView, NotificationInfo data, int position) {
        this.data = data;

        txtName.setText(data.getTargetUser().getNickname());
        BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(data.getTargetUser()), imgPhoto, ImageConfigUtils.getLargePhotoConfig());

        // desc时间
        String from = data.getCreate_at();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(Long.parseLong(from));
        String dateStr = dateformat.format(gc.getTime());
        txtDesc.setText(dateStr);

        BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(data.getUserInfo()), imgPhoto_, ImageConfigUtils.getLargePhotoConfig());
        String n = "@"+data.getUserInfo().getNickname();
        txtName_.setText(n);
        text_.setContent(data.getStatusContent().getText());
        txtContent.setContent(data.getText());


        if (String.valueOf(data.getType()).startsWith("1")){
            //1开头证明是点赞通知，所以不显示回复按钮
            reply.setVisibility(View.GONE);
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

}
