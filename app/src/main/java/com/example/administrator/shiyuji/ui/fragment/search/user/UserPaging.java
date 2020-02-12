package com.example.administrator.shiyuji.ui.fragment.search.user;

import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfos;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;
import com.example.administrator.shiyuji.util.common.AisenUtils;

/**
 * Created by Administrator on 2019/12/7.
 */

public class UserPaging implements IPaging<UserInfo, UserInfos> {

    private static final long serialVersionUID = -1563104012290641720L;

    private String firstId;

    private String lastId;

    private String last;

    @Override
    public void processData(UserInfos newDatas, UserInfo firstData, UserInfo lastData) {
        if (firstData != null)
            firstId = String.valueOf(firstData.getId());
        if (lastData != null){
            lastId = String.valueOf(lastData.getId());
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

