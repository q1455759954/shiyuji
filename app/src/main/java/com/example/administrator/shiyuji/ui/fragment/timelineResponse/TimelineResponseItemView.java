package com.example.administrator.shiyuji.ui.fragment.timelineResponse;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.support.action.DoLikeAction;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.MyTextView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2019/8/30.
 */


public class TimelineResponseItemView extends ARecycleViewItemView<StatusComment> implements View.OnClickListener, DoLikeAction.OnLikeCallback {

    public static final int LAYOUT_RES = R.layout.item_timeline_response;

    private ABaseFragment fragment;
    private BizFragment bizFragment;
    private int textSize = 0;

    @ViewInject(id = R.id.txtName)
    TextView txtName;
    @ViewInject(id=R.id.imgPhoto)
    CircleImageView circleImageView;
    @ViewInject(id = R.id.txtDesc)
    TextView txtDesc;
    @ViewInject(id = R.id.likeNum)
    TextView likeNum;
    @ViewInject(id = R.id.txtContent)
    MyTextView txtContent;
    @ViewInject(id = R.id.imgLike)
    ImageView imgLike;

    private StatusComment data;

    public TimelineResponseItemView(View convertView, ABaseFragment fragment) {
        super(fragment.getActivity(), convertView);
        this.fragment = fragment;
        bizFragment = BizFragment.createBizFragment(fragment);

        textSize = AppSettings.getTextSize();
    }

    @Override
    public void onBindData(View convertView, StatusComment data, int position) {
        this.data=data;
        txtName.setText(data.getUserInfo().getNickname());
        BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(data.getUserInfo()), circleImageView, ImageConfigUtils.getLargePhotoConfig());

        String from = data.getCreated_at();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(Long.parseLong(from));
        String dateStr = dateformat.format(gc.getTime());
        txtDesc.setText(dateStr);

        likeNum.setText(String.valueOf(data.getLikedCount()));
        txtContent.setContent(data.getText());

        setLikeView();

    }

    /**
     * 设置点赞图片
     */
    public void setLikeView() {
        LikeBean likeBean = DoLikeAction.likeCache.get(data.getRow_key());
        if (likeBean==null){
            likeBean= LikeDB.get(data.getRow_key());
        }
        if (imgLike != null) {
            imgLike.setTag(data);
            imgLike.setOnClickListener(this);

            if (likeBean != null && likeBean.isLiked()) {
                imgLike.setSelected(true);

                if (data.getAttitudes_count() > 0)
                    likeNum.setText(AisenUtils.getCounter(data.getAttitudes_count(), "+1"));
                else
                    likeNum.setText("1");

            }else if (likeBean==null && data.isLiked()){
                imgLike.setSelected(data.isLiked());

                if (data.getAttitudes_count() > 0)
                    likeNum.setText(AisenUtils.getCounter(data.getAttitudes_count(), "+1"));
                else
                    likeNum.setText("1");
            } else {
                imgLike.setSelected(false);
                if (data.getAttitudes_count() > 0)
                    likeNum.setText(AisenUtils.getCounter(data.getAttitudes_count() , ""));
                else
                    likeNum.setText("");
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
