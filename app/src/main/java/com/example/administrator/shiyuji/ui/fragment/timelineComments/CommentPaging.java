package com.example.administrator.shiyuji.ui.fragment.timelineComments;

import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComments;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;

/**
 * Created by Administrator on 2019/7/4.
 */

public class CommentPaging implements IPaging<StatusComment, StatusComments> {

    private static final long serialVersionUID = -2363918217556704381L;

    private String firstId;

    private String lastId;

    @Override
    public void processData(StatusComments newDatas, StatusComment firstData, StatusComment lastData) {
        if (firstData != null)
            firstId = AisenUtils.getId(firstData);
        if (lastData != null)
            lastId = AisenUtils.getId(lastData);
    }

    @Override
    public String getPreviousPage() {
        return firstId;
    }

    @Override
    public String getNextPage() {

        return lastId;
    }

}
