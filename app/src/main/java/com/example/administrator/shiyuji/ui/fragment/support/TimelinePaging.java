package com.example.administrator.shiyuji.ui.fragment.support;

import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;

/**
 * Created by Administrator on 2019/7/1.
 */

public class TimelinePaging implements IPaging<StatusContent, StatusContents> {

    private static final long serialVersionUID = -1563104012290641720L;

    private String firstId;

    private String lastId;

    private String last;

    @Override
    public void processData(StatusContents newDatas, StatusContent firstData, StatusContent lastData) {
        if (firstData != null)
            firstId = AisenUtils.getId(firstData);
        if (lastData != null){
            lastId = AisenUtils.getId(lastData);
            last = lastData.getRe_row_key();
        }

    }

    @Override
    public String getPreviousPage() {
        return firstId;
    }

    @Override
    public String getNextPage() {

        return lastId;
    }
    public String getLast(){
        return last;
    }
}

