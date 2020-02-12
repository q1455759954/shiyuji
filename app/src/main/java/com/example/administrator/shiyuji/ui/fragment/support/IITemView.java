package com.example.administrator.shiyuji.ui.fragment.support;

import android.view.View;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/6/29.
 */

public interface IITemView<T extends Serializable> {
    void onBindView(View var1);

    void onBindData(View var1, T var2, int var3);

    int itemPosition();

    void reset(int var1, int var2);

    int itemSize();

    View getConvertView();
}
