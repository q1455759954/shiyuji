package com.example.administrator.shiyuji.ui.fragment.mainFragment.life.item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.ui.activity.publish.PublishActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.SearchInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

/**
 * Created by Administrator on 2019/10/18.
 */

public class LifeDefFragment extends ALifeFragment {

    public static LifeDefFragment newInstance(String type) {
        LifeDefFragment fragment = new LifeDefFragment();

        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);

        return fragment;
    }
    public static LifeDefFragment newInstance(String type,UserInfo userInfo) {
        LifeDefFragment fragment = new LifeDefFragment();

        Bundle args = new Bundle();
        args.putSerializable("userInfo",userInfo);
        args.putString("type", type);
        fragment.setArguments(args);

        return fragment;
    }

    public static LifeDefFragment newInstance(String method, SearchInfo searchInfo) {
        LifeDefFragment fragment = new LifeDefFragment();

        Bundle args = new Bundle();
        args.putSerializable("searchInfo",searchInfo);
        args.putString("method", method);
        args.putString("type", method);
        fragment.setArguments(args);

        return fragment;
    }


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
        if (type.equals("commodity")){
            LAYOUT_RES = R.layout.item_life_commodity;
        }else {
            LAYOUT_RES = R.layout.item_appointment;
        }
    }


    @Override
    public IItemViewCreator<LifeInfo> configItemViewCreator() {
        return new IItemViewCreator<LifeInfo>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {

                return inflater.inflate(LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<LifeInfo> newItemView(View convertView, int viewType) {

                return new LifeItemView(convertView, LifeDefFragment.this);
            }

        };
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
        new DefLifeTask(mode).execute();
    }

    class DefLifeTask extends ALifeTask {

        public DefLifeTask(RefreshMode mode) {
            super(mode);
        }


        @Override
        public LifeInfos getStatusContents(Params params) throws TaskException {

            if (searchInfo!=null){
                LifeInfos lifeInfos = new LifeInfos();
                if (isFirstTime){
                    if (type.equals("commodity")){
                        lifeInfos.setLifeInfos(searchInfo.getCommodityInfo());
                    }else {
                        lifeInfos.setLifeInfos(searchInfo.getWorkInfo());
                    }
                    isFirstTime=false;
                    return lifeInfos;
                }else {
                    params.addParameter("since_id", String.valueOf(searchInfo.getCountPerPage()));
                    params.addParameter("type","lifeInfos");
                    params.addParameter("queryStr",searchInfo.getQueryStr());
                    SearchInfo result = SinaSDK.getInstance(getTaskCacheMode(this), AppContext.getAccount().getAccessToken()).getNextPage(params);
                    int s = searchInfo.getCountPerPage();
                    searchInfo = result;
                    searchInfo.setCountPerPage(s+1);
                    if (type.equals("commodity")){
                        lifeInfos.setLifeInfos(searchInfo.getCommodityInfo());
                    }else {
                        lifeInfos.setLifeInfos(searchInfo.getWorkInfo());
                    }
                    return lifeInfos;
                }

            }

            params.addParameter("page",type);//用来客户端记录缓存
            params.addParameter("type",type);//用来服务端判断
            try {
                LifeInfos lifeInfos = null;
                if(userInfo!=null){
                    params.addParameter("user_id", String.valueOf(userInfo.getId()));
//                    params.addParameter("type", String.valueOf(isFirstTime));
//                    isFirstTime = false;
//                    //除了第一次后每一次都是disable,增加flag判断
//                    if (getTaskCacheMode(this)==ABizLogic.CacheMode.disable && flag){
//                        params.addParameter("type","true");
//                        flag=false;
//                    }
                    lifeInfos = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).getUserLifeInfo(params);
                }else{
                    lifeInfos = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).getLifeInfo(params);
                }


                return lifeInfos;
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
