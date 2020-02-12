package com.example.administrator.shiyuji.ui.fragment.timelineResponse;

import android.view.View;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.ANotificationFragment;
import com.example.administrator.shiyuji.ui.fragment.support.AHeaderItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.MyTextView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2019/8/30.
 */

public abstract class ATimelineResponseHeaderView extends ARecycleViewItemView<StatusComment> implements View.OnClickListener {

    public static final int LAYOUT_RES = R.layout.item_timeline_response_header;

    private ATimelineResponsePagerFragment fragment;
    private StatusComment mStatusComment;

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



    public ATimelineResponseHeaderView(ATimelineResponsePagerFragment fragment, View itemView, StatusComment mStatusComment) {
        super(fragment.getActivity(), itemView);
        this.fragment = fragment;
        itemView.setOnClickListener(this);
        this.mStatusComment=mStatusComment;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBindData(View convertView, StatusComment data, int position) {

    }

    @Override
    public void onBindView(View convertView) {
        super.onBindView(convertView);
//        setHeaderView();
        txtName.setText(mStatusComment.getUserInfo().getNickname());
        BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(mStatusComment.getUserInfo()), circleImageView, ImageConfigUtils.getLargePhotoConfig());

        String from = mStatusComment.getCreated_at();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(Long.parseLong(from));
        String dateStr = dateformat.format(gc.getTime());
        txtDesc.setText(dateStr);

        likeNum.setText(String.valueOf(mStatusComment.getLikedCount()));
        txtContent.setContent(mStatusComment.getText());
    }
    private void setHeaderView() {
        String[] titles = fragment.getResources().getStringArray(getTitleArrRes());
//        txtTitle.setText(titles[getFeaturePosition()]);
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
