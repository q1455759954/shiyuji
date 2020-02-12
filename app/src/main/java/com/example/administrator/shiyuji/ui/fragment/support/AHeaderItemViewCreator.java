package com.example.administrator.shiyuji.ui.fragment.support;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

/**
 * 顶部视图生成器
 */

public abstract class AHeaderItemViewCreator<T extends Serializable> implements IItemViewCreator<T> {
    public AHeaderItemViewCreator() {
    }

    /**
     * 页面布局xml文件
     * @param inflater
     * @param parent
     * @param viewType
     * @return
     */
    public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        int[][] var4 = this.setHeaders();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            int[] headerLayoutRes = var4[var6];
            if(viewType == headerLayoutRes[1]) {
                return inflater.inflate(headerLayoutRes[0], parent, false);
            }
        }

        return null;
    }

    public abstract int[][] setHeaders();
}
