package com.example.administrator.shiyuji.ui.fragment.support;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;

import java.io.Serializable;

/**
 * 底部视图组件与数据绑定，并且监听页面变化
 */

public class BasicFooterView<T extends Serializable> extends AFooterItemView<T> {
    public static final int LAYOUT_RES;
    private View footerView;
    @ViewInject(
            idStr = "btnMore"
    )
    TextView btnMore;
    @ViewInject(
            idStr = "layLoading"
    )
    View layLoading;
    @ViewInject(
            idStr = "txtLoading"
    )
    TextView txtLoading;

    public BasicFooterView(Activity context, View itemView, OnFooterViewCallback callback) {
        super(context, itemView, callback);
        this.footerView = itemView;
        InjectUtility.initInjectedView(context, this, this.getConvertView());
        this.btnMore.setVisibility(View.VISIBLE);
        this.layLoading.setVisibility(View.GONE);
        this.btnMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(BasicFooterView.this.getCallback() != null && BasicFooterView.this.getCallback().canLoadMore()) {
                    BasicFooterView.this.getCallback().onLoadMore();
                }

            }
        });
    }

    public void onBindView(View convertView) {
    }

    public void onBindData(View convertView, T data, int position) {
    }

    public View getConvertView() {
        return this.footerView;
    }

    public void onTaskStateChanged(AFooterItemView<?> footerItemView, ABaseFragment.ABaseTaskState state, TaskException exception, APagingFragment.RefreshMode mode) {
        if(state == ABaseFragment.ABaseTaskState.finished) {
            if(mode == APagingFragment.RefreshMode.update && this.layLoading.getVisibility() == View.VISIBLE) {
                this.layLoading.setVisibility(View.GONE);
                this.btnMore.setVisibility(View.VISIBLE);
            }
        } else if(state == ABaseFragment.ABaseTaskState.prepare) {
            if(mode == APagingFragment.RefreshMode.update) {
                this.txtLoading.setText(this.loadingText());
                this.layLoading.setVisibility(View.VISIBLE);
                this.btnMore.setVisibility(View.GONE);
                this.btnMore.setText(this.moreText());
            }
        } else if(state == ABaseFragment.ABaseTaskState.success) {
            if(mode == APagingFragment.RefreshMode.update || mode == APagingFragment.RefreshMode.reset) {
                if(!this.getCallback().canLoadMore()) {
                    this.btnMore.setText(this.endpagingText());
                } else {
                    this.btnMore.setText(this.moreText());
                }
            }
        } else if(state == ABaseFragment.ABaseTaskState.falid && mode == APagingFragment.RefreshMode.update && this.layLoading.getVisibility() == 0) {
            this.btnMore.setText(this.faildText());
        }

    }

    public void setFooterViewToRefreshing() {
        if(this.layLoading.getVisibility() != View.VISIBLE) {
            this.layLoading.setVisibility(View.VISIBLE);
            this.getCallback().onLoadMore();
        }

    }

    protected String moreText() {
        return this.getContext().getString(R.string.comm_footer_more);
    }

    protected String loadingText() {
        return this.getContext().getString(R.string.comm_footer_loading);
    }

    protected String endpagingText() {
        return this.getContext().getString(R.string.comm_footer_pagingend);
    }

    protected String faildText() {
        return this.getContext().getString(R.string.comm_footer_faild);
    }

    static {
        LAYOUT_RES = R.layout.comm_lay_footerview;
    }
}
