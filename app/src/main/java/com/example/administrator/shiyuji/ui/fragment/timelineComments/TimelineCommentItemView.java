package com.example.administrator.shiyuji.ui.fragment.timelineComments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.support.action.DoLikeAction;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineItemView;
import com.example.administrator.shiyuji.ui.fragment.timelineResponse.TimelineResponsePagerFragment;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.widget.MyTextView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Administrator on 2019/7/4.
 */

public class TimelineCommentItemView extends ARecycleViewItemView<StatusComment> implements View.OnClickListener, DoLikeAction.OnLikeCallback{

    public static final int LAYOUT_RES = R.layout.item_timeline_comment;

    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;
    @ViewInject(id = R.id.txtName)
    TextView txtName;
    @ViewInject(id = R.id.txtDesc)
    TextView txtDesc;
    @ViewInject(id = R.id.txtContent)
    MyTextView txtContent;
    @ViewInject(id=R.id.responseName)
    TextView responseName;
    @ViewInject(id=R.id.txtReContent)
    MyTextView txtReContent;

    @ViewInject(id = R.id.layRe)
    View layRe;
    @ViewInject(id = R.id.likeNum)
    TextView likeNum;
    @ViewInject(id = R.id.responseNum)
    TextView responseNum;


    @ViewInject(id = R.id.imgLike)
    ImageView imgLike;


    private ABaseFragment mFragment;
    private BizFragment bizFragment;
    private StatusComment statusComment;
    int firstTop;
    int normalTop;
    private Context context;

    public TimelineCommentItemView(ABaseFragment fragment, View itemView) {
        super(fragment.getActivity(), itemView);

        this.mFragment = fragment;
        this.context = GlobalContext.getInstance();
        bizFragment = BizFragment.createBizFragment(fragment);

        firstTop = Utils.dip2px(getContext(), 16);
        normalTop = Utils.dip2px(getContext(), 8);
    }

    @Override
    public void onBindData(View convertView, StatusComment data, int position) {
        statusComment = data;
        UserInfo user = data.getUserInfo();
        if (user != null) {
            BitmapLoader.getInstance().display(mFragment,
                    AisenUtils.getUserPhoto(user),
                    imgPhoto, ImageConfigUtils.getLargePhotoConfig());
            bizFragment.userShow(imgPhoto, user);
            txtName.setText(AisenUtils.getUserScreenName(user));
        }
        else {
            bizFragment.userShow(imgPhoto, null);
            txtName.setText(R.string.error_cmts);
            imgPhoto.setImageResource(R.drawable.user_placeholder);
        }

        // 文本
        String text = data.getText();
        txtContent.setContent(text);

        if (data.getReply_comment()!=null){
            layRe.setVisibility(View.VISIBLE);
            txtReContent.setContent(data.getReply_comment().getText());
            responseName.setText(data.getReply_comment().getUserInfo().getNickname());
        }else {
            layRe.setVisibility(View.GONE);
        }
        responseNum.setText("共"+data.getResponse_count()+"条回复  ＞");
        likeNum.setText(String.valueOf(data.getLikedCount()));

        // desc时间
        String from = data.getCreated_at();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(Long.parseLong(from));
        String dateStr = dateformat.format(gc.getTime());
        txtDesc.setText(dateStr);

        int top = position == 0 ? firstTop : normalTop;
        convertView.setPadding(convertView.getPaddingLeft(), top, convertView.getPaddingRight(), convertView.getPaddingBottom());

        //跳转到回复页面的监听事件
        responseNum.setOnClickListener(listener);

        setLikeView();

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra("userInfo",statusComment.getUserInfo());
                getContext().startActivity(intent);
            }
        });

//        btnLike.setTag(data);
//        setLikeBtn(data);
//        btnLike.setOnClickListener(this);
    }

    /**
     * 设置点赞图片
     */
    public void setLikeView() {
        LikeBean likeBean = DoLikeAction.likeCache.get(statusComment.getRow_key());
        if (likeBean==null){
            likeBean= LikeDB.get(statusComment.getRow_key());
        }
        if (imgLike != null) {
            imgLike.setTag(statusComment);
            imgLike.setOnClickListener(this);

            if (likeBean != null && likeBean.isLiked()) {
                imgLike.setSelected(true);

                if (statusComment.getAttitudes_count() > 0)
                    likeNum.setText(AisenUtils.getCounter(statusComment.getAttitudes_count(), "+1"));
                else
                    likeNum.setText("1");

            }else if (likeBean==null && statusComment.isLiked()){
                imgLike.setSelected(statusComment.isLiked());

                if (statusComment.getAttitudes_count() > 0)
                    likeNum.setText(AisenUtils.getCounter(statusComment.getAttitudes_count(), "+1"));
                else
                    likeNum.setText("1");
            } else {
                imgLike.setSelected(false);
                if (statusComment.getAttitudes_count() > 0)
                    likeNum.setText(AisenUtils.getCounter(statusComment.getAttitudes_count() , ""));
                else
                    likeNum.setText("");
            }
        }
    }

    /**
     * 跳转到回复页面的监听器
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimelineResponsePagerFragment.launch(getContext(), statusComment);
        }
    };

    private void setLikeBtn(StatusComment comment) {
//        if (comment.isLiked()) {
//            if (comment.getLikedCount() > 0)
//                btnLike.setText(String.format(context.getString(R.string.attitudes_format), AisenUtils.getCounter(comment.getLikedCount(), "+1")));
//            else
//                btnLike.setText(String.format(context.getString(R.string.attitudes_format), "+1"));
//        }
//        else {
//            if (comment.getLikedCount() > 0)
//                btnLike.setText(String.format(context.getString(R.string.attitudes_format), AisenUtils.getCounter(comment.getLikedCount())));
//            else
//                btnLike.setText("赞");
//        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnCmt:
//                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(findViewById(R.id.test_bug), InputMethodManager.RESULT_SHOWN);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//
//                TimelineDetailPagerFragment.launch(getContext(),statusComment);
                break;
            case R.id.imgLike:
                StatusComment comment = (StatusComment) v.getTag();


                LikeBean likeBean = DoLikeAction.likeCache.get(comment.getRow_key());
                if (likeBean==null){
                    likeBean=LikeDB.get(comment.getRow_key());
                }
                boolean like = likeBean == null? !comment.isLiked():!likeBean.isLiked();

                v.findViewById(R.id.imgLike).setSelected(like);

                bizFragment.doLike(comment, like, v, this);
                break;

        }
    }

    @Override
    public void onLikeFaild(BizFragment.ILikeBean data) {
        setLikeView();
    }

    @Override
    public void onLikeSuccess(BizFragment.ILikeBean data, View likeView) {
        if (getContext() == null)
            return;


        setLikeView();

        if (likeView.getTag() == data) {
            bizFragment.animScale(likeView);
        }
    }
}

