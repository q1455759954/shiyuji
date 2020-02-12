package com.example.administrator.shiyuji.ui.fragment.support;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.administrator.shiyuji.util.viewutil.InjectUtility;

import java.io.Serializable;

/**
 * ViewHolder基类，定义了该有的方法，子类实现具体的界面绑定
 */
public abstract class ARecycleViewItemView<T extends Serializable> extends RecyclerView.ViewHolder implements IITemView<T> {
    private int size;
    private int position;
    private final View convertView;
    private final Activity context;

    public ARecycleViewItemView(Activity context, View itemView) {
        super(itemView);
        this.context = context;
        this.convertView = itemView;
    }

    public void onBindView(View convertView) {
        InjectUtility.initInjectedView(this.getContext(), this, convertView);
    }

    public void reset(int size, int position) {
        this.size = size;
        this.position = position;
    }

    public int itemSize() {
        return this.size;
    }

    public int itemPosition() {
        return this.position;
    }

    public View getConvertView() {
        return this.convertView;
    }

    public Activity getContext() {
        return this.context;
    }
}
