package com.example.administrator.shiyuji.sdk.cache;

import com.example.administrator.shiyuji.util.network.IResult;

/**
 * Created by Administrator on 2019/7/8.
 */


public class ResultBean implements IResult {
    private boolean outofdate;
    private boolean fromCache;
    private boolean endPaging;
    private String[] pagingIndex;

    public ResultBean() {
    }

    public boolean outofdate() {
        return this.isOutofdate();
    }

    public boolean fromCache() {
        return this.isFromCache();
    }

    public boolean endPaging() {
        return this.isEndPaging();
    }

    public String[] pagingIndex() {
        return this.getPagingIndex();
    }

    public boolean isOutofdate() {
        return this.outofdate;
    }

    public void setOutofdate(boolean outofdate) {
        this.outofdate = outofdate;
    }

    public boolean isFromCache() {
        return this.fromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }

    public boolean isEndPaging() {
        return this.endPaging;
    }

    public void setEndPaging(boolean endPaging) {
        this.endPaging = endPaging;
    }

    public String[] getPagingIndex() {
        return this.pagingIndex;
    }

    public void setPagingIndex(String[] pagingIndex) {
        this.pagingIndex = pagingIndex;
    }
}
