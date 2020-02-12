package com.example.administrator.shiyuji.ui.fragment.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.annotation.ViewInject;

import java.io.Serializable;

/**
 * 设置recycleView的下拉刷新
 */

public abstract class ARecycleViewSwipeRefreshFragment<T extends Serializable, Ts extends Serializable, Header extends Serializable> extends ARecycleViewFragment<T, Ts, Header> implements SwipeRefreshLayout.OnRefreshListener {
    @ViewInject(
            idStr = "swipeRefreshLayout"
    )
    public SwipeRefreshLayout swipeRefreshLayout;

    public ARecycleViewSwipeRefreshFragment() {
    }



    public int inflateContentView() {
        return R.layout.comm_ui_recycleview_swiperefresh;
    }

    protected void setupRefreshView(Bundle savedInstanceSate) {
        super.setupRefreshView(savedInstanceSate);
        this.setupSwipeRefreshLayout();
    }

    protected void setupSwipeRefreshLayout() {
        this.getSwipeRefreshLayout().setOnRefreshListener(this);
        this.getSwipeRefreshLayout().setColorSchemeResources(new int[]{17170459, 17170452, 17170456, 17170454});
    }

    /**
     * 下滑刷新
     */
    public void onRefresh() {
        this.onPullDownToRefresh();
    }

    public boolean setRefreshViewToLoading() {
        this.getSwipeRefreshLayout().setRefreshing(true);
        return false;
    }

    /**
     * 刷新完成后执行这个
     * @param mode
     */
    public void onRefreshViewFinished(RefreshMode mode) {
        if(mode != RefreshMode.update && this.getSwipeRefreshLayout().isRefreshing()) {
            this.getSwipeRefreshLayout().setRefreshing(false);
        }

    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return this.swipeRefreshLayout;
    }
}

