package com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.support;

import android.app.Activity;
import android.view.View;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.util.common.AppSettings;

/**
 * 通知列表内容
 * Created by Administrator on 2019/8/8.
 */

public class NotificationItemView extends ARecycleViewItemView<StatusContent> implements View.OnClickListener {

    public static final int LAYOUT_RES = R.layout.item_notification;

    private ABaseFragment fragment;
    private BizFragment bizFragment;
    private int textSize = 0;

    public NotificationItemView(View convertView, ABaseFragment fragment) {
        super(fragment.getActivity(), convertView);
        this.fragment = fragment;
        bizFragment = BizFragment.createBizFragment(fragment);

        textSize = AppSettings.getTextSize();
    }

    @Override
    public void onBindData(View var1, StatusContent var2, int var3) {

    }

    @Override
    public void onClick(View v) {

    }


}
