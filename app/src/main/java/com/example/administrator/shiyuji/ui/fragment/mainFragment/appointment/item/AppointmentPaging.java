package com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item;

import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;
import com.example.administrator.shiyuji.util.common.AisenUtils;

/**
 * Created by Administrator on 2019/10/8.
 */

public class AppointmentPaging implements IPaging<AppointmentInfo, AppointmentInfos> {

    private static final long serialVersionUID = -1563104012290641720L;

    private String firstId;

    private String lastId;

    @Override
    public void processData(AppointmentInfos newDatas, AppointmentInfo firstData, AppointmentInfo lastData) {
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
