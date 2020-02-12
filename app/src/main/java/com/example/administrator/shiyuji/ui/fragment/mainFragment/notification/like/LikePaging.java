package com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.like;

import android.app.Notification;

import com.example.administrator.shiyuji.ui.fragment.bean.NotificationInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.NotificationInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;
import com.example.administrator.shiyuji.util.common.AisenUtils;

/**
 * Created by Administrator on 2019/9/27.
 */

public class LikePaging implements IPaging<NotificationInfo, NotificationInfos> {

    private static final long serialVersionUID = -1563104012290641720L;

    private String firstId;

    private String lastId;

    @Override
    public void processData(NotificationInfos newDatas, NotificationInfo firstData, NotificationInfo lastData) {
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
