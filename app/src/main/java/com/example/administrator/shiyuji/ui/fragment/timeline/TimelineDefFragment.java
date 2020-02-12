package com.example.administrator.shiyuji.ui.fragment.timeline;


import android.app.Activity;
import android.os.Bundle;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.activity.publish.PublishActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.bean.SearchInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.support.TimelinePaging;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.sdk.base.ABizLogic;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.lang.reflect.Method;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * 默认微博列表
 *
 */
public class TimelineDefFragment extends ATimelineFragment {

    public static TimelineDefFragment newInstance(String method) {
        TimelineDefFragment fragment = new TimelineDefFragment();

        Bundle args = new Bundle();
        args.putString("method", method);
        fragment.setArguments(args);

        return fragment;
    }


    public static TimelineDefFragment newInstance(String method,String statusType) {
        TimelineDefFragment fragment = new TimelineDefFragment();

        Bundle args = new Bundle();
        args.putString("method", method);
        args.putString("statusType", statusType);
        fragment.setArguments(args);

        return fragment;
    }

    public static TimelineDefFragment newInstance(String method,UserInfo userInfo) {
        TimelineDefFragment fragment = new TimelineDefFragment();

        Bundle args = new Bundle();
        args.putSerializable("userInfo",userInfo);
        args.putString("method", method);
        fragment.setArguments(args);

        return fragment;
    }

    public static TimelineDefFragment newInstance(String method,SearchInfo searchInfo) {
        TimelineDefFragment fragment = new TimelineDefFragment();

        Bundle args = new Bundle();
        args.putSerializable("searchInfo",searchInfo);
        args.putString("method", method);
        fragment.setArguments(args);

        return fragment;
    }

    public static void launch(String method, Activity activity) {
        FragmentArgs args = new FragmentArgs();

        args.add("method", method);

        SinaCommonActivity.launch(activity, TimelineDefFragment.class, args);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BaseActivity activity = (BaseActivity) getActivity();
        String title = null;
        if (method.equals("getCollection")){
            title = "我的收藏";
        }else if (method.equals("getMyLike")){
            title = "我的赞";
        }
        if (title!=null){
            ((BaseActivity) getActivity()).getSupportActionBar().setTitle(title);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }


    //执行SinaSDK的哪个方法
    private String statusType;
    private String method;
    private UserInfo userInfo;
    private SearchInfo searchInfo;
    private boolean isFirstTime = true;
    private boolean flag = true;
    private int curPage = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFirstTime = savedInstanceState == null || (savedInstanceState.getBoolean("isFirstTime"));
        userInfo = savedInstanceState == null ? (UserInfo) getArguments().getSerializable("userInfo") :(UserInfo) savedInstanceState.getSerializable("userInfo");
        searchInfo = savedInstanceState == null ? (SearchInfo) getArguments().getSerializable("searchInfo") :(SearchInfo) savedInstanceState.getSerializable("searchInfo");

        method = savedInstanceState == null ? getArguments().getString("method")
                : savedInstanceState.getString("method");
        statusType = savedInstanceState == null ? getArguments().getString("statusType")
                : savedInstanceState.getString("statusType");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFirstTime", isFirstTime);
        outState.putString("method", method);
        outState.putString("statusType", statusType);
        outState.putSerializable("userInfo", userInfo);
        outState.putSerializable("searchInfo", searchInfo);
    }

    @Override
    public void requestData(RefreshMode mode) {
        new DefTimelineTask(mode).execute();
    }


    class DefTimelineTask extends ATimelineTask {

        public DefTimelineTask(RefreshMode mode) {
            super(mode);
        }


        @Override
        public StatusContents getStatusContents(Params params) throws TaskException {
            StatusContents statusContents = new StatusContents();
            if (searchInfo!=null){
                if (searchInfo.getContentList()==null)
                    return null;


                if (isFirstTime){
                    statusContents.setStatuses(searchInfo.getContentList());
                    isFirstTime=false;
                    return statusContents;
                }else {
                    params.addParameter("since_id", String.valueOf(searchInfo.getCountPerPage()));
                    params.addParameter("type","statusContent");
                    params.addParameter("queryStr",searchInfo.getQueryStr());
                    SearchInfo result = SinaSDK.getInstance(getTaskCacheMode(this), AppContext.getAccount().getAccessToken()).getNextPage(params);
                    int s = searchInfo.getCountPerPage();
                    searchInfo = result;
                    searchInfo.setCountPerPage(s+1);

                    Setting action = getSetting("base_url");
                    String base_url = action.getValue().split("/")[2].split(":")[0];
                    for (StatusContent statusContent:searchInfo.getContentList()){
                        if (statusContent.getPic_urls()!=null){
                            for (PicUrls picUrls : statusContent.getPic_urls()){
                                picUrls.setThumbnail_pic(picUrls.getThumbnail_pic().replaceAll("localhost",base_url));
                            }
                        }
                        if (statusContent.getVideoInfo()!=null){
                            statusContent.getVideoInfo().setPic_url(statusContent.getVideoInfo().getPic_url().replaceAll("localhost",base_url));
                            statusContent.getVideoInfo().setVideo_url(statusContent.getVideoInfo().getVideo_url().replaceAll("localhost",base_url));
                        }
                        statusContent.getUserInfo().setAvatar(statusContent.getUserInfo().getAvatar().replaceAll("localhost",base_url));
                    }

                    statusContents.setStatuses(searchInfo.getContentList());

                    return statusContents;
                }

            }



            params.addParameter("page",method);//用来客户端记录缓存
            params.addParameter("type",statusType);//用来服务端判断
            try {
                if(userInfo!=null){
                    params.addParameter("user_id", String.valueOf(userInfo.getId()));
                    params.addParameter("type", String.valueOf(isFirstTime));
                    isFirstTime = false;
                    //除了第一次后每一次都是disable,增加flag判断
                    if (getTaskCacheMode(this)==ABizLogic.CacheMode.disable && flag){
                        params.addParameter("type","true");
                        flag=false;
                    }
                }
                if (method.equals("getCollection")||method.equals("getMyLike")){
                    TimelinePaging paging = (TimelinePaging) getPaging();
                    params.addParameter("max_id",paging.getLast());
                }
                if (statusType!=null&&statusType.equals("recommend")){
                    params.addParameter("since_id", String.valueOf(curPage));
                    curPage++;
                }
                Method timelineMethod = SinaSDK.class.getMethod(method, new Class[] { Params.class });
                //getTaskCacheMode(this)),这个方法，第一次执行时account为1，此时从缓存中取，再次执行时，account将大于1，所以不会从缓存中取
                 statusContents = (StatusContents) timelineMethod.invoke(SinaSDK.getInstance(getTaskCacheMode(this), AppContext.getAccount().getAccessToken()),params);
                if (!statusContents.fromCache()){
                    flag=false;
                }
                return statusContents;
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

    private String getPageName() {
        if ("statusesFriendsTimeLine".equals(method)) {
            return getString(R.string.timeline_all);
        } else if ("statusesBilateralTimeLine".equals(method)) {
            return getString(R.string.timeline_bilateral);
        } else if ("statusesToMe".equals(method)) {
            return getString(R.string.timeline_tome);
        }

        return getString(R.string.timeline_all);
    }

}

