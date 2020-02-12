package com.example.administrator.shiyuji.ui.fragment.timeline;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.fragment.menu.CardMenuBuilder;

/**
 * Created by Administrator on 2019/6/29.
 */


public abstract class ATimelineHeaderView extends ARecycleViewItemView<StatusContent> implements View.OnClickListener {

    public static final int LAYOUT_RES = R.layout.headerview_profile_timeline;

    private ATimelineFragment fragment;

    public static final String[] timelineFeatureArr = { "0", "1", "2", "3", "5" };

    public static final String[] profileFeatureArr = { "0", "1", "2", "3" };

    @ViewInject(id = R.id.txtName)
    TextView txtTitle;

    public ATimelineHeaderView(ATimelineFragment fragment, View itemView) {
        super(fragment.getActivity(), itemView);

        this.fragment = fragment;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onBindView(View convertView) {
        super.onBindView(convertView);

        setHeaderView();
    }

    @Override
    public void onBindData(View convertView, StatusContent data, int position) {

    }

    @Override
    public void onClick(View v) {
        if (fragment.isRefreshing())
            return;

        String[] titles = fragment.getResources().getStringArray(getTitleArrRes());

        CardMenuBuilder cardMenuBuilder = new CardMenuBuilder(getContext(), v, AisenUtils.getCardMenuOptions());
        for (int i = 0; i < titles.length; i++) {
            cardMenuBuilder.add(i, titles[i]);
        }
        cardMenuBuilder.setOnCardMenuCallback(new CardMenuBuilder.OnCardMenuCallback() {

            @Override
            public boolean onCardMenuItemSelected(MenuItem menuItem) {
                if (getFeaturePosition() == menuItem.getItemId()) {
                    return true;
                }

                fragment.setFeature(getTitleFeature()[menuItem.getItemId()]);

                // 清理线程状态，可以加载缓存
                fragment.cleatTaskCount(APagingFragment.PAGING_TASK_ID);

                setHeaderView();

                fragment.requestDataDelaySetRefreshing(200);
                return true;
            }

        });
        cardMenuBuilder.show();
    }

    private void setHeaderView() {
        String[] titles = fragment.getResources().getStringArray(getTitleArrRes());
        txtTitle.setText(titles[getFeaturePosition()]);
    }

    private int getFeaturePosition() {
        for (int i = 0; i < getTitleFeature().length; i++) {
            if (getTitleFeature()[i].equals(fragment.getFeature()))
                return i;
        }

        return 0;
    }

    abstract protected int getTitleArrRes();

    abstract protected String[] getTitleFeature();

}

