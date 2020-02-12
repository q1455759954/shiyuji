package com.example.administrator.shiyuji.ui.fragment.mainFragment.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.jauker.widget.BadgeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/8/9.
 */
public class NotificationDefFragment extends ANotificationFragment {


    public NotificationDefFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void requestData(RefreshMode mode) {
        new DefNotificationTask(mode).execute();
    }


    class DefNotificationTask extends ANotificationTask {

        public DefNotificationTask(RefreshMode mode) {
            super(mode);
        }


        @Override
        public StatusContents getStatusContents(Params params) throws TaskException {

//            try {
//                Method timelineMethod = SinaSDK.class.getMethod(method, new Class[] { Params.class });
//                return (StatusContents) timelineMethod.invoke(SinaSDK.getInstance(ABizLogic.CacheMode.auto), params);
//            } catch (Throwable e) {
//                if (e.getCause() instanceof TaskException) {
//                    throw (TaskException) e.getCause();
//                }
//            }


            //            PicUrls picUrls = new PicUrls();
            PicUrls picUrls1 = new PicUrls();

            picUrls1.setThumbnail_pic("http://wx2.sinaimg.cn/large/723dd72fly1g46v07dvajj20zl1r01l1.jpg");

//
            StatusContent statusContent = new StatusContent();

            List<StatusContent> list = new ArrayList<>();
            list.add(statusContent);

            StatusContents s = new StatusContents();
            s.setStatuses(list);

            return s;
//            throw new TaskException(TaskException.TaskError.resultIllegal.toString());
        }
//            http://wx2.sinaimg.cn/large/723dd72fly1g46v07dvajj20zl1r01l1.jpg


    }

}
