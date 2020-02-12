package com.example.administrator.shiyuji.ui.fragment.publish;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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
 * Created by Administrator on 2019/7/7.
 */

public abstract class AGridFragment<T extends Serializable, Ts extends Serializable, Header extends Serializable> extends APagingFragment<T, Ts, Header, GridView> implements AdapterView.OnItemClickListener {
    @ViewInject(
            idStr = "gridview"
    )
    GridView gridView;

    public AGridFragment() {
    }

    public int inflateContentView() {
        return R.layout.comm_ui_gridview;
    }

    public GridView getRefreshView() {
        return this.gridView;
    }

    protected void setupRefreshView(Bundle savedInstanceSate) {
        super.setupRefreshView(savedInstanceSate);
        this.getRefreshView().setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    protected IPagingAdapter<T> newAdapter(ArrayList<T> datas) {
        return new BasicListAdapter(this, datas);
    }

    protected void bindAdapter(IPagingAdapter adapter) {
        if(this.getRefreshView().getAdapter() == null) {
            this.getRefreshView().setAdapter((BasicListAdapter)adapter);
        }

    }

    protected void addFooterViewToRefreshView(AFooterItemView<?> footerItemView) {
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
                this.getRefreshView().smoothScrollToPosition(0);
                this.getAdapter().notifyDataSetChanged();
            }

        }
    }
}
