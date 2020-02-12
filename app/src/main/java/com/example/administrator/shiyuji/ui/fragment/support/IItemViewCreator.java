package com.example.administrator.shiyuji.ui.fragment.support;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/6/29.
 */

public interface IItemViewCreator<T extends Serializable> {
    View newContentView(LayoutInflater var1, ViewGroup var2, int var3);

    IITemView<T> newItemView(View var1, int var2);
}