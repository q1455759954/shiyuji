package com.example.administrator.shiyuji.ui.fragment.mainFragment.life.item;

import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfos;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;
import com.example.administrator.shiyuji.util.common.AisenUtils;

/**
 * Created by Administrator on 2019/10/18.
 */

public class LifePaging implements IPaging<LifeInfo, LifeInfos> {

    private static final long serialVersionUID = -1563104012290641720L;

    private String firstId;

    private String lastId;

    @Override
    public void processData(LifeInfos newDatas, LifeInfo firstData, LifeInfo lastData) {
        if (firstData != null)
            firstId = AisenUtils.getId(firstData.getCommodity()==null?firstData.getWorkInfo():firstData.getCommodity());
        if (lastData != null)
            lastId = AisenUtils.getId(lastData.getCommodity()==null?lastData.getWorkInfo():lastData.getCommodity());
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
