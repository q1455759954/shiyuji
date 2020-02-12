package com.example.administrator.shiyuji.ui.fragment.support;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/6/29.
 */

public interface IPaging<T extends Serializable, Ts extends Serializable> extends Serializable {
    void processData(Ts var1, T var2, T var3);

    String getPreviousPage();

    String getNextPage();

}
