package com.example.administrator.shiyuji.ui.fragment.support;

import android.app.Activity;
import android.view.View;

import java.io.Serializable;

/**
 * ViewHolder的子类，实现OnFooterViewListener接口，上滑刷新onLoadMore
 */

public abstract class AFooterItemView<T extends Serializable> extends ARecycleViewItemView<T> implements OnFooterViewListener {
    private AFooterItemView.OnFooterViewCallback onFooterViewCallback;

    public AFooterItemView(Activity context, View itemView, AFooterItemView.OnFooterViewCallback callback) {
        super(context, itemView);
        this.onFooterViewCallback = callback;
    }

    protected AFooterItemView.OnFooterViewCallback getCallback() {
        return this.onFooterViewCallback;
    }

    public interface OnFooterViewCallback {
        void onLoadMore();

        boolean canLoadMore();
    }
}