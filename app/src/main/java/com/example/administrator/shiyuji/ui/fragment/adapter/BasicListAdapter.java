package com.example.administrator.shiyuji.ui.fragment.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IPagingAdapter;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Administrator on 2019/7/4.
 */

public class BasicListAdapter<T extends Serializable> extends BaseAdapter implements IPagingAdapter {
    private APagingFragment holderFragment;
    private IItemViewCreator<T> itemViewCreator;
    private ArrayList<T> datas;

    public BasicListAdapter(APagingFragment holderFragment, ArrayList<T> datas) {
        if(datas == null) {
            datas = new ArrayList();
        }

        this.holderFragment = holderFragment;
        this.itemViewCreator = holderFragment.configItemViewCreator();
        this.datas = datas;
    }

    public int getItemViewType(int position) {
        Serializable t = (Serializable)this.getDatas().get(position);
        return t instanceof ItemTypeData?((ItemTypeData)t).itemType():1000;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        IITemView itemView;
        if(convertView == null) {
            int itemType = this.getItemViewType(position);
            convertView = this.itemViewCreator.newContentView(this.holderFragment.getActivity().getLayoutInflater(), parent, itemType);
            itemView = this.itemViewCreator.newItemView(convertView, itemType);
            itemView.onBindView(convertView);
            convertView.setTag(R.id.itemview, itemView);
        } else {
            itemView = (IITemView)convertView.getTag(R.id.itemview);
        }

        itemView.reset(this.datas.size(), position);
        itemView.onBindData(convertView, (Serializable)this.datas.get(position), position);
        return convertView;
    }

    public ArrayList<T> getDatas() {
        return this.datas;
    }

    public int getCount() {
        return this.datas.size();
    }

    public Object getItem(int position) {
        return this.datas.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }
}

