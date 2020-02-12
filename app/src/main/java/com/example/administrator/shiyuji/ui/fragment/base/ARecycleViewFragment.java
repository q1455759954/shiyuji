package com.example.administrator.shiyuji.ui.fragment.base;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.ui.fragment.support.AFooterItemView;
import com.example.administrator.shiyuji.ui.fragment.support.AHeaderItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.adapter.BasicRecycleViewAdapter;
import com.example.administrator.shiyuji.ui.fragment.support.IPagingAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 找到xml视图，绑定具体的适配器，为视图增加addOnScrollListener
 */

public abstract class ARecycleViewFragment<T extends Serializable, Ts extends Serializable, Header extends Serializable> extends APagingFragment<T, Ts, Header, RecyclerView> implements AdapterView.OnItemClickListener {
    @ViewInject(
            idStr = "recycleview"
    )
    RecyclerView mRecycleView;

    public ARecycleViewFragment() {
    }

    public int inflateContentView() {
        return R.layout.comm_ui_recycleview;
    }

    public RecyclerView getRefreshView() {
        return this.mRecycleView;
    }

    /**
     * 设置上滑刷新
     * @param config
     */
    protected void setupRefreshConfig(RefreshConfig config) {
        super.setupRefreshConfig(config);

        this.getRefreshView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                ARecycleViewFragment.this.onScrollStateChanged(newState);//上滑刷新
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        this.getRefreshView().setLayoutManager(this.configLayoutManager());
    }

    protected IPagingAdapter<T> newAdapter(ArrayList<T> datas) {
        return new BasicRecycleViewAdapter(this.getActivity(), this, this.configItemViewCreator(), datas);
    }

    protected RecyclerView.LayoutManager configLayoutManager() {
        return new LinearLayoutManager(this.getActivity());
    }

    protected void bindAdapter(IPagingAdapter adapter) {
        if(this.getRefreshView().getAdapter() == null) {
            this.getRefreshView().setAdapter((BasicRecycleViewAdapter)adapter);
        }

        if(((BasicRecycleViewAdapter)this.getAdapter()).getOnItemClickListener() != this) {
            ((BasicRecycleViewAdapter)this.getAdapter()).setOnItemClickListener(this);
        }

    }

    protected void addFooterViewToRefreshView(AFooterItemView<?> footerItemView) {
        ((BasicRecycleViewAdapter)this.getAdapter()).addFooterView(footerItemView);
    }

    protected void addHeaderViewToRefreshView(AHeaderItemViewCreator<?> headerItemViewCreator) {
        ((BasicRecycleViewAdapter)this.getAdapter()).setHeaderItemViewCreator(headerItemViewCreator);
    }

    protected void setupRefreshView(Bundle savedInstanceSate) {
        super.setupRefreshView(savedInstanceSate);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    protected void toLastReadPosition() {
        super.toLastReadPosition();
        if(this.getRefreshView() != null && !TextUtils.isEmpty(this.refreshConfig.positionKey) && this.getLastReadPosition() >= 0) {
            if(this.getRefreshView().getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager manager = (LinearLayoutManager)this.getRefreshView().getLayoutManager();
                if(this.getAdapterItems().size() > this.getLastReadPosition()) {
                    manager.scrollToPositionWithOffset(this.getLastReadPosition(), this.getLastReadTop() + this.getRefreshView().getPaddingTop());
                }
            }

        }
    }

    protected int getFirstVisiblePosition() {
        if(this.getRefreshView().getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager)this.getRefreshView().getLayoutManager();
            return manager.findFirstVisibleItemPosition();
        } else {
            return 0;
        }
    }
}
