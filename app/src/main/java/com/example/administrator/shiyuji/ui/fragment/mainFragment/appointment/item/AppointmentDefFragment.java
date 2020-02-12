package com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item;

import android.os.Bundle;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.ui.activity.publish.PublishActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.SearchInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

/**
 * Created by Administrator on 2019/10/8.
 */

public class AppointmentDefFragment extends AAppointmentFragment {

    public static AppointmentDefFragment newInstance(String type) {
        AppointmentDefFragment fragment = new AppointmentDefFragment();

        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);

        return fragment;
    }
    public static AppointmentDefFragment newInstance(String type,UserInfo userInfo) {
        AppointmentDefFragment fragment = new AppointmentDefFragment();

        Bundle args = new Bundle();
        args.putSerializable("userInfo",userInfo);
        args.putString("type", type);
        fragment.setArguments(args);

        return fragment;
    }

    public static AppointmentDefFragment newInstance(String method, SearchInfo searchInfo) {
        AppointmentDefFragment fragment = new AppointmentDefFragment();

        Bundle args = new Bundle();
        args.putSerializable("searchInfo",searchInfo);
        args.putString("method", method);
        fragment.setArguments(args);

        return fragment;
    }

    public static final String ACTION_REFRESH_CMT_CREATE = "com.shiyuji.ACTION_REFRESH_CMT_CREATE";


    //执行SinaSDK的哪个方法
    private String type;
    private UserInfo userInfo;
    private boolean isFirstTime = true;
    private boolean flag = true;
    private SearchInfo searchInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFirstTime = true;
        userInfo = savedInstanceState == null ? (UserInfo) getArguments().getSerializable("userInfo") :(UserInfo) savedInstanceState.getSerializable("userInfo");
        searchInfo = savedInstanceState == null ? (SearchInfo) getArguments().getSerializable("searchInfo") :(SearchInfo) savedInstanceState.getSerializable("searchInfo");

        type = savedInstanceState == null ? getArguments().getString("type")
                : savedInstanceState.getString("type");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFirstTime = true;
        outState.putString("type", type);
        outState.putSerializable("userInfo", userInfo);
        outState.putSerializable("searchInfo", searchInfo);
    }

    @Override
    public void requestData(RefreshMode mode) {
        new DefAppointmentTask(mode).execute();
    }

    class DefAppointmentTask extends AAppointmentTask {

        public DefAppointmentTask(RefreshMode mode) {
            super(mode);
        }


        @Override
        public AppointmentInfos getStatusContents(Params params) throws TaskException {

            if (searchInfo != null) {
                AppointmentInfos app = new AppointmentInfos();
                if (isFirstTime) {
                    app.setAppointmentInfos(searchInfo.getAppointmentInfoList());
                    isFirstTime = false;
                    return app;
                } else {
                    params.addParameter("since_id", String.valueOf(searchInfo.getCountPerPage()));
                    params.addParameter("type", "appointmentInfo");
                    params.addParameter("queryStr",searchInfo.getQueryStr());
                    SearchInfo result = SinaSDK.getInstance(getTaskCacheMode(this), AppContext.getAccount().getAccessToken()).getNextPage(params);
                    int s = searchInfo.getCountPerPage();
                    searchInfo = result;
                    searchInfo.setCountPerPage(s+1);
                    app.setAppointmentInfos(searchInfo.getAppointmentInfoList());
                    return app;
                }
            }

            params.addParameter("page",type);//用来客户端记录缓存
            params.addParameter("type",type);//用来服务端判断
            try {
                AppointmentInfos appointmentInfos =null;
                if(userInfo!=null){
                    params.addParameter("user_id", String.valueOf(userInfo.getId()));
//                    params.addParameter("type", String.valueOf(isFirstTime));
//                    isFirstTime = false;
//                    //除了第一次后每一次都是disable,增加flag判断
//                    if (getTaskCacheMode(this)==ABizLogic.CacheMode.disable && flag){
//                        params.addParameter("type","true");
//                        flag=false;
//                    }
                    appointmentInfos = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).getUserAppointment(params);
                }else {
                    appointmentInfos = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).getAppointment(params);
                }


                return appointmentInfos;
            } catch (Throwable e) {
                if (e.getCause() instanceof TaskException) {
                    throw (TaskException) e.getCause();
                }
            }

            throw new TaskException(TaskException.TaskError.resultIllegal.toString());
        }
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

//    private String getPageName() {
//        if ("statusesFriendsTimeLine".equals(method)) {
//            return getString(R.string.timeline_all);
//        } else if ("statusesBilateralTimeLine".equals(method)) {
//            return getString(R.string.timeline_bilateral);
//        } else if ("statusesToMe".equals(method)) {
//            return getString(R.string.timeline_tome);
//        }
//
//        return getString(R.string.timeline_all);
//    }


}
