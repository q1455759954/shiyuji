package com.example.administrator.shiyuji.util.network;

/**
 * Created by Administrator on 2019/6/28.
 */

public interface IResult {
    boolean outofdate();

    boolean fromCache();

    boolean endPaging();

    String[] pagingIndex();
}

