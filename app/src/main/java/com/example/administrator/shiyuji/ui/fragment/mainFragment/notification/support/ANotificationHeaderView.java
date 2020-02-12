package com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.support;

import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.ui.activity.tuikit.ConversationActivity;
import com.example.administrator.shiyuji.ui.fragment.adapter.BasicRecycleViewAdapter;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.ANotificationFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.like.LikeDefFragment;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.jauker.widget.BadgeView;

import q.rorbin.badgeview.QBadgeView;

/**
 * Created by Administrator on 2019/8/8.
 */

public abstract class ANotificationHeaderView extends ARecycleViewItemView<StatusContent> implements View.OnClickListener {

    public static final int LAYOUT_RES = R.layout.item_notification_top;

    private ANotificationFragment fragment;

    @ViewInject(id = R.id.txtName)
    TextView txtTitle;
//    @ViewInject(id = R.id.re_aitewode)
//    RelativeLayout re_aitewode;
    @ViewInject(id = R.id.re_like)
    RelativeLayout re_like;
    @ViewInject(id = R.id.re_chat)
    RelativeLayout re_chat;
    @ViewInject(id = R.id.re_comment)
    RelativeLayout re_comment;

//    @ViewInject(id= R.id.wode_t)
//    TextView wode_t;
    @ViewInject(id= R.id.like_t)
    TextView like_t;
    @ViewInject(id= R.id.comment_t)
    TextView comment_t;
    @ViewInject(id= R.id.chat_t)
    TextView chat_t;

    public ANotificationHeaderView(ANotificationFragment fragment, View itemView) {
        super(fragment.getActivity(), itemView);
        this.fragment = fragment;
        itemView.setOnClickListener(this);
        likeBadgeView = new BadgeView(getContext());
        commentBadgeView = new BadgeView(getContext());
        chatBadgeView = new BadgeView(getContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.re_chat:
                ConversationActivity.lunch(getContext());
                break;
            case R.id.re_like:
                LikeDefFragment.launch(getContext(),"likeMe");
                //修改通知数量为0
                AppContext.getAccount().getUnreadCount().setLike_num(0);
                LikeDB.update(AppContext.getAccount().getUnreadCount());
                //更新界面
                BasicRecycleViewAdapter adapter = (BasicRecycleViewAdapter) fragment.getAdapter();
                adapter.getHeaderView().onHiddenChanged();
                break;
            case R.id.re_comment:
                LikeDefFragment.launch(getContext(),"comment");
                //修改通知数量为0
                AppContext.getAccount().getUnreadCount().setComment_num(0);
                LikeDB.update(AppContext.getAccount().getUnreadCount());
                break;
//            case R.id.re_aitewode:
//                break;
        }

    }

    private  BadgeView likeBadgeView = null;
    private  BadgeView commentBadgeView = null;
    private  BadgeView chatBadgeView = null;


    public void onHiddenChanged() {

        if (AppContext.getAccount().getUnreadCount()!=null){
            if (AppContext.getAccount().getUnreadCount().getLike_num()!=0){
                likeBadgeView.setTargetView(like_t);
                likeBadgeView.setBadgeCount(AppContext.getAccount().getUnreadCount().getLike_num());
                likeBadgeView.setBadgeGravity(Gravity.CENTER_VERTICAL);
                likeBadgeView.setVisibility(View.VISIBLE);
            }else {
                likeBadgeView.setTargetView(like_t);
                likeBadgeView.setBadgeCount(AppContext.getAccount().getUnreadCount().getLike_num());
                likeBadgeView.setBadgeGravity(Gravity.CENTER_VERTICAL);
                likeBadgeView.setVisibility(View.GONE);
            }

            if (AppContext.getAccount().getUnreadCount().getChat()!=0){
                chatBadgeView.setTargetView(chat_t);
                chatBadgeView.setBadgeCount(AppContext.getAccount().getUnreadCount().getLike_num());
                chatBadgeView.setBadgeGravity(Gravity.CENTER_VERTICAL);
                chatBadgeView.setVisibility(View.VISIBLE);
            }else {
                chatBadgeView.setTargetView(chat_t);
                chatBadgeView.setBadgeCount(AppContext.getAccount().getUnreadCount().getLike_num());
                chatBadgeView.setBadgeGravity(Gravity.CENTER_VERTICAL);
                chatBadgeView.setVisibility(View.GONE);
            }


            if (AppContext.getAccount().getUnreadCount().getComment_num()!=0){
                commentBadgeView.setTargetView(comment_t);
                commentBadgeView.setBadgeCount(AppContext.getAccount().getUnreadCount().getLike_num());
                commentBadgeView.setBadgeGravity(Gravity.CENTER_VERTICAL);
                commentBadgeView.setVisibility(View.VISIBLE);
            }else {
                commentBadgeView.setTargetView(comment_t);
                commentBadgeView.setBadgeCount(AppContext.getAccount().getUnreadCount().getLike_num());
                commentBadgeView.setBadgeGravity(Gravity.CENTER_VERTICAL);
                commentBadgeView.setVisibility(View.GONE);
            }

        }



    }

    @Override
    public void onBindData(View var1, StatusContent var2, int var3) {

    }

    @Override
    public void onBindView(View convertView) {
        super.onBindView(convertView);
        re_chat.setOnClickListener(this);
//        re_aitewode.setOnClickListener(this);
        re_like.setOnClickListener(this);
        re_comment.setOnClickListener(this);
        onHiddenChanged();

//        setHeaderView();
    }
    private void setHeaderView() {
        String[] titles = fragment.getResources().getStringArray(getTitleArrRes());
        txtTitle.setText(titles[getFeaturePosition()]);
    }
    private int getFeaturePosition() {
//        for (int i = 0; i < getTitleFeature().length; i++) {
//            if (getTitleFeature()[i].equals(fragment.getFeature()))
//                return i;
//        }

        return 0;
    }

    abstract protected int getTitleArrRes();

    abstract protected String[] getTitleFeature();
}
