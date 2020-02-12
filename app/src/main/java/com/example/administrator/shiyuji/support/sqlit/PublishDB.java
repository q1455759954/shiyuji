package com.example.administrator.shiyuji.support.sqlit;

import com.example.administrator.shiyuji.sdk.cache.Extra;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.support.sqlit.extra.FieldUtils;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/8/6.
 */

public class PublishDB {

    public static void addPublish(PublishBean bean, UserInfo user) {
        Extra extra = new Extra(String.valueOf(user.getId()), null);
        SinaDB.getDB().insertOrReplace(extra, bean);
    }

    /**
     * 获取添加状态的发布消息
     *
     * @param user
     * @return
     */
    public static List<PublishBean> getPublishOfAddStatus(UserInfo user) {
        try {
            //format后面对应的是%s,而selectionArgs里面的参数对应selection中的?
            String selection = String.format(" %s = ? and %s = ? ", FieldUtils.OWNER, "status");
            String[] selectionArgs = { String.valueOf(user.getId()) ,"create"};

            return SinaDB.getDB().select(PublishBean.class, selection, selectionArgs);
        } catch (Exception e) {
        }

        return new ArrayList<PublishBean>();
    }

}
