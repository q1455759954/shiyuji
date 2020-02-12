package com.example.administrator.shiyuji.ui.fragment.search.user;

import android.app.Activity;
import android.os.Bundle;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.activity.publish.PublishActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.SearchInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfos;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * Created by Administrator on 2019/12/7.
 */

public class UserDefFragment  extends AUserFragment {

    public static UserDefFragment newInstance(String method,SearchInfo searchInfo) {
        UserDefFragment fragment = new UserDefFragment();

        Bundle args = new Bundle();
        args.putSerializable("searchInfo",searchInfo);
        args.putString("method", method);
        fragment.setArguments(args);

        return fragment;
    }

    public static void launch(String method, Activity from) {
        FragmentArgs args = new FragmentArgs();

        args.add("method", method);

        SinaCommonActivity.launch(from, UserDefFragment.class, args);
    }


    //执行SinaSDK的哪个方法
    private String method;
    private UserInfo userInfo;
    private SearchInfo searchInfo;
    private boolean isFirstTime = true;
    private boolean flag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFirstTime = savedInstanceState == null || (savedInstanceState.getBoolean("isFirstTime"));
        userInfo = savedInstanceState == null ? (UserInfo) getArguments().getSerializable("userInfo") :(UserInfo) savedInstanceState.getSerializable("userInfo");
        searchInfo = savedInstanceState == null ? (SearchInfo) getArguments().getSerializable("searchInfo") :(SearchInfo) savedInstanceState.getSerializable("searchInfo");

        method = savedInstanceState == null ? getArguments().getString("method")
                : savedInstanceState.getString("method");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFirstTime", isFirstTime);
        outState.putString("method", method);
        outState.putSerializable("userInfo", userInfo);
        outState.putSerializable("searchInfo", searchInfo);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BaseActivity activity = (BaseActivity) getActivity();
        String title = null;
        if (method.equals("attends")){
            title = "关注列表";
        }else if (method.equals("fans")){
            title = "粉丝列表";
        }
        if (title!=null){
            ((BaseActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    int page=0;


    @Override
    public void requestData(RefreshMode mode) {
        new DefUserTask(mode).execute();
    }


    class DefUserTask extends AUserTask {

        public DefUserTask(RefreshMode mode) {
            super(mode);
        }


        @Override
        public UserInfos getStatusContents(Params params) throws TaskException {
            if (searchInfo!=null){
                UserInfos userInfos = new UserInfos();
                if (isFirstTime){
                    userInfos.setUserInfoList(searchInfo.getUserInfoList());
                    isFirstTime=false;
                    return userInfos;
                }else {
                    params.addParameter("since_id", String.valueOf(searchInfo.getCountPerPage()));
                    params.addParameter("type","userInfo");
                    params.addParameter("queryStr",searchInfo.getQueryStr());
                    SearchInfo result = SinaSDK.getInstance(getTaskCacheMode(this), AppContext.getAccount().getAccessToken()).getNextPage(params);
                    int s = searchInfo.getCountPerPage();
                    searchInfo = result;
                    searchInfo.setCountPerPage(s+1);

                    Setting action = getSetting("base_url");
                    String base_url = action.getValue().split("/")[2].split(":")[0];
                    for (UserInfo userInfo:searchInfo.getUserInfoList()){
                        userInfo.setAvatar(userInfo.getAvatar().replaceAll("localhost",base_url));
                    }

                    userInfos.setUserInfoList(searchInfo.getUserInfoList());
                    return userInfos;
                }
            }

            params.addParameter("type",method);
            params.addParameter("max_id", String.valueOf(page));
            UserInfos result = SinaSDK.getInstance(getTaskCacheMode(this), AppContext.getAccount().getAccessToken()).getUserInfos(params);
            if (result!=null){
                Setting action = getSetting("base_url");
                String base_url = action.getValue().split("/")[2].split(":")[0];
                for (UserInfo userInfo:result.getUserInfoList()){
                    userInfo.setAvatar(userInfo.getAvatar().replaceAll("localhost",base_url));
                }
                page+=1;
                return result;
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

