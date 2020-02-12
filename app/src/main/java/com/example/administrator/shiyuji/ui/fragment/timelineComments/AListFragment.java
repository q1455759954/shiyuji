package com.example.administrator.shiyuji.ui.fragment.timelineComments;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.ui.fragment.adapter.BasicListAdapter;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.support.AFooterItemView;
import com.example.administrator.shiyuji.ui.fragment.support.AHeaderItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IPagingAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2019/7/4.
 */

public abstract class AListFragment<T extends Serializable, Ts extends Serializable, Header extends Serializable> extends APagingFragment<T, Ts, Header, ListView> implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    @ViewInject(
            idStr = "listView"
    )
    ListView mListView;

    public AListFragment() {
    }

    public int inflateContentView() {
        return R.layout.comm_ui_list;
    }

    protected void setupRefreshView(Bundle savedInstanceSate) {
        super.setupRefreshView(savedInstanceSate);
        this.getRefreshView().setOnScrollListener(this);
        this.getRefreshView().setOnItemClickListener(this);
    }

    public ListView getRefreshView() {
        return this.mListView;
    }

    protected IPagingAdapter<T> newAdapter(ArrayList<T> datas) {
        return new BasicListAdapter(this, datas);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.onScroll(firstVisibleItem, visibleItemCount, totalItemCount);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.onScrollStateChanged(scrollState);
    }

    public void requestDataOutofdate() {
        this.getRefreshView().setSelectionFromTop(0, 0);
        super.requestDataOutofdate();
    }

    protected int getFirstVisiblePosition() {
        return this.getRefreshView().getFirstVisiblePosition();
    }

    protected void bindAdapter(IPagingAdapter adapter) {
        if(this.getRefreshView().getAdapter() == null) {
            this.getRefreshView().setAdapter((BasicListAdapter)adapter);
        }

    }

    protected void addFooterViewToRefreshView(AFooterItemView<?> footerItemView) {
        this.getRefreshView().addFooterView(footerItemView.getConvertView());
    }

    protected void addHeaderViewToRefreshView(AHeaderItemViewCreator<?> headerItemViewCreator) {
    }

    public void setItems(ArrayList<T> items) {
        if(items != null) {
            this.setViewVisiable(this.loadingLayout, 8);
            this.setViewVisiable(this.loadFailureLayout, 8);
            if(items.size() == 0 && this.emptyLayout != null) {
                this.setViewVisiable(this.emptyLayout, 0);
                this.setViewVisiable(this.contentLayout, 8);
            } else {
                this.setViewVisiable(this.emptyLayout, 8);
                this.setViewVisiable(this.contentLayout, 0);
            }

            this.setAdapterItems(items);
            if(this.getRefreshView().getAdapter() == null) {
                this.bindAdapter(this.getAdapter());
            } else {
                this.getRefreshView().setSelectionFromTop(0, 0);
                this.getAdapter().notifyDataSetChanged();
            }

        }
    }
}
