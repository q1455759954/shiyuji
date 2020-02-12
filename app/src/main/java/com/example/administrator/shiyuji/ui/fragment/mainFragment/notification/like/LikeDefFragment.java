package com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.like;

import android.app.Activity;
import android.os.Bundle;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.activity.publish.PublishActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.NotificationInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2019/9/27.
 */

public class LikeDefFragment extends ALikeFragment {


    public static void launch(Activity from, String type) {
        FragmentArgs args = new FragmentArgs();

        args.add("type", type);

        SinaCommonActivity.launch(from, LikeDefFragment.class, args);
    }

    //执行SinaSDK的哪个方法
    private String type;
    private UserInfo userInfo;
    private boolean isFirstTime = true;
    private boolean flag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFirstTime = true;
        userInfo = savedInstanceState == null ? (UserInfo) getArguments().getSerializable("userInfo") :(UserInfo) savedInstanceState.getSerializable("userInfo");
        type = savedInstanceState == null ? getArguments().getString("type")
                : savedInstanceState.getString("type");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFirstTime = true;
        outState.putString("type", type);
        outState.putSerializable("userInfo", userInfo);
    }

    @Override
    public void requestData(RefreshMode mode) {
        new DefLikeTask(mode).execute();
    }

    class DefLikeTask extends ALikeFragment.ALikeTask {

        public DefLikeTask(RefreshMode mode) {
            super(mode);
        }


        @Override
        public NotificationInfos getStatusContents(Params params) throws TaskException {
            params.addParameter("page",type);//用来客户端记录缓存
            params.addParameter("type",type);//用来服务端判断
            try {
//                if(userInfo!=null){
//                    params.addParameter("user_id", String.valueOf(userInfo.getId()));
//                    params.addParameter("type", String.valueOf(isFirstTime));
//                    isFirstTime = false;
//                    //除了第一次后每一次都是disable,增加flag判断
//                    if (getTaskCacheMode(this)== ABizLogic.CacheMode.disable && flag){
//                        params.addParameter("type","true");
//                        flag=false;
//                    }
//                }
                Method timelineMethod = SinaSDK.class.getMethod("getNotification", Params.class);
                //getTaskCacheMode(this)),这个方法，第一次执行时account为1，此时从缓存中取，再次执行时，account将大于1，所以不会从缓存中取
                NotificationInfos notificationInfos = (NotificationInfos) timelineMethod.invoke(SinaSDK.getInstance(getTaskCacheMode(this), AppContext.getAccount().getAccessToken()),params);
                if (!notificationInfos.fromCache()){
                    flag=false;
                }
                return notificationInfos;
            } catch (Throwable e) {
                if (e.getCause() instanceof TaskException) {
                    throw (TaskException) e.getCause();
                }
            }

            throw new TaskException(TaskException.TaskError.resultIllegal.toString());
        }
//            http://wx2.sinaimg.cn/large/723dd72fly1g46v07dvajj20zl1r01l1.jpg

//            PicUrls picUrls = new PicUrls();
//            PicUrls picUrls1 = new PicUrls();
//            PicUrls picUrls2 = new PicUrls();
//            PicUrls picUrls3 = new PicUrls();
//            PicUrls picUrls4 = new PicUrls();
//            picUrls.setThumbnail_pic("http://wx2.sinaimg.cn/large/723dd72fly1g46v07dvajj20zl1r01l1.jpg");
//            picUrls1.setThumbnail_pic("http://wx1.sinaimg.cn/thumbnail/006Y6CSFgy1g4o5muxz1cj30c80haju9.jpg");
//            picUrls2.setThumbnail_pic("http://wx4.sinaimg.cn/thumbnail/006WxBySgy1g289c8xf9hj30j60y30y2.jpg");
//            picUrls3.setThumbnail_pic("http://wx3.sinaimg.cn/thumbnail/006WxBySgy1g289c8ul9ij30j60y3wjz.jpg");
//            picUrls4.setThumbnail_pic("http://wx2.sinaimg.cn/thumbnail/006WxBySgy1g289c8lujsj30j60y3mxz.jpg");
//
//
//            StatusContent statusContent = new StatusContent(1,"[笑哈哈]哈哈哈哈哈","b",new UserInfo("1","木木","1"),null,1,new PicUrls[]{picUrls1});
//            StatusContent statusContent2 = new StatusContent(2,"[泪流满面]可以了吗？","b",new UserInfo("1","小粑","1"),null,1,new PicUrls[]{picUrls,picUrls1});
//            StatusContent statusContent3 = new StatusContent(3,"[泪流满面]可以了吗？","b",new UserInfo("1","小粑","1"),null,1,new PicUrls[]{picUrls,picUrls1,picUrls2});
//            StatusContent statusContent4 = new StatusContent(3,"[泪流满面]可以了吗？","b",new UserInfo("1","小粑","1"),null,1,new PicUrls[]{picUrls,picUrls1,picUrls2,picUrls3});
//            StatusContent statusContent5 = new StatusContent(3,"[泪流满面]可以了吗？","b",new UserInfo("1","小粑","1"),null,1,new PicUrls[]{picUrls1,picUrls3,picUrls2,picUrls1,picUrls3});
//            StatusContent statusContent6 = new StatusContent(3,"[泪流满面]可以了吗？","b",new UserInfo("1","小粑","1"),null,1,new PicUrls[]{picUrls,picUrls1,picUrls4,picUrls4,picUrls2,picUrls3,picUrls4,picUrls4,picUrls2});
//
//            List<StatusContent> list = new ArrayList<>();
//            list.add(statusContent);
//            list.add(statusContent2);
//            list.add(statusContent3);
//            list.add(statusContent4);
//            list.add(statusContent5);
//            list.add(statusContent6);
//            StatusContents s = new StatusContents(list);
    }


    @Override
    public boolean onToolbarDoubleClick() {
//        requestDataDelaySetRefreshing(AppSettings.REQUEST_DATA_DELAY);
//        getRefreshView().scrollToPosition(0);

        PublishActivity.publishStatus(getActivity(), null);

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

//        UMengUtil.onPageStart(getActivity(), getPageName());
    }

    @Override
    public void onPause() {
        super.onPause();

//        UMengUtil.onPageEnd(getActivity(), getPageName());
    }


}
