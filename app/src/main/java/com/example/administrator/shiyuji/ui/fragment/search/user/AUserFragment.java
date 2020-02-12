package com.example.administrator.shiyuji.ui.fragment.search.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.adapter.BasicRecycleViewAdapter;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.base.ARecycleViewSwipeRefreshFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfos;
import com.example.administrator.shiyuji.ui.fragment.support.AHeaderItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.BasicFooterView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;
import com.example.administrator.shiyuji.ui.fragment.support.IPagingAdapter;
import com.example.administrator.shiyuji.ui.fragment.support.TimelinePaging;
import com.example.administrator.shiyuji.ui.fragment.timeline.ATimelineFragment;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineItemView;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineDetailPagerFragment;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/12/7.
 */

public abstract class AUserFragment extends ARecycleViewSwipeRefreshFragment<UserInfo, UserInfos, UserInfo> {

    private String feature = "0";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        if (getActivity() instanceof AppointmentFragment) {
//            BizFragment.createBizFragment(getActivity()).getFabAnimator().attachToRecyclerView(getRefreshView(), null, null);
//        }

        if (getArguments() == null) {
            feature = savedInstanceState == null ? feature
                    : savedInstanceState.getString("feature", "0");
        } else {
            feature = savedInstanceState == null ? getArguments().getString("feature", "0")
                    : savedInstanceState.getString("feature", "0");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("feature", feature);
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);

    }


    @Override
    protected AHeaderItemViewCreator<UserInfo> configHeaderViewCreator() {
//        if (this instanceof TimelineDefFragment) {
//            return new AHeaderItemViewCreator<StatusContent>() {
//
//                @Override
//                public int[][] setHeaders() {
//                    return new int[][]{ { ATimelineHeaderView.LAYOUT_RES, 100 } };
//                }
//
//                @Override
//                public IITemView<StatusContent> newItemView(View convertView, int viewType) {
//                    return new ATimelineHeaderView(ATimelineFragment.this, convertView) {
//
//                        @Override
//                        protected int getTitleArrRes() {
//                            return R.array.timeline_headers;
//                        }
//
//                        @Override
//                        protected String[] getTitleFeature() {
//                            return ATimelineHeaderView.timelineFeatureArr;
//                        }
//
//                    };
//                }
//
//            };
//        }

        return super.configHeaderViewCreator();
    }

    @Override
    public IItemViewCreator<UserInfo> configItemViewCreator() {
        return new IItemViewCreator<UserInfo>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {

                return inflater.inflate(UserItemView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<UserInfo> newItemView(View convertView, int viewType) {

                return new UserItemView(convertView, AUserFragment.this);
            }

        };
    }


    @Override
    protected IPagingAdapter<UserInfo> newAdapter(ArrayList<UserInfo> datas) {
        return new UserAdapter(configItemViewCreator(), datas);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra("userInfo",getAdapterItems().get(position));
        startActivity(intent);

//        TimelineDetailPagerFragment.launch(getActivity(), getAdapterItems().get(position));
    }

    @Override
    protected IPaging<UserInfo, UserInfos> newPaging() {
        return new UserPaging();
    }

    @Override
    protected IItemViewCreator<UserInfo> configFooterViewCreator() {
        return new IItemViewCreator<UserInfo>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(BasicFooterView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<UserInfo> newItemView(View convertView, int viewType) {
                return new BasicFooterView<UserInfo>(getActivity(), convertView, AUserFragment.this) {

                    @Override
                    protected String endpagingText() {
                        return getString(R.string.disable_status);
                    }

                    @Override
                    protected String loadingText() {
                        return String.format(getString(R.string.loading_status), timelineCount());
                    }

                };
            }

        };
    }

    protected int timelineCount() {
        return AppSettings.getTimelineCount();
    }

    class UserAdapter extends BasicRecycleViewAdapter<UserInfo> {

        public UserAdapter(IItemViewCreator<UserInfo> itemViewCreator, ArrayList<UserInfo> datas) {
            super(getActivity(), AUserFragment.this, itemViewCreator, datas);
        }

        @Override
        public int getItemViewType(int position) {
            int itemType = super.getItemViewType(position);

            // 如果不是HeaderView和FooterView
            if (itemType == IPagingAdapter.TYPE_NORMAL) {
                return Integer.parseInt(getFeature());
            }

            return itemType;
        }

    }

    abstract public class AUserTask extends APagingTask<Void, Void, UserInfos> {

        public AUserTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<UserInfo> parseResult(UserInfos userInfos) {
            return userInfos.getUserInfoList();
        }

        @Override
        protected UserInfos workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {

            Params params = new Params();

            //如果是更新数据，将第一条数据的id记下来
            if (mode == APagingFragment.RefreshMode.refresh && !TextUtils.isEmpty(previousPage))
                params.addParameter("since_id", "0");
            //刷新数据，记下最后一条数据
            if (mode == APagingFragment.RefreshMode.update && !TextUtils.isEmpty(nextPage))
                params.addParameter("max_id", nextPage);

            if (params.getParameter("since_id")!=null){
                Log.d("看","previousPage为："+previousPage);
            }else if (params.getParameter("max_id")!=null){
                Log.d("看","nextPage为："+nextPage);
            }
            params.addParameter("count", String.valueOf(AppSettings.getTimelineCount()));//数量

            UserInfos result = getStatusContents(params);


            for (UserInfo userInfo : result.getUserInfoList()){

                userInfo.setAvatar(userInfo.getAvatar().replaceAll("localhost","192.168.43.209"));
            }


            return result;
        }

        @Override
        protected boolean handleResult(RefreshMode mode, List<UserInfo> datas) {
            // 如果是重置或者刷新数据，加载数据大于分页大小，则清空之前的数据
            if (mode == RefreshMode.refresh) {
//                 目前微博加载分页大小是默认大小
//                if (datas.size() >= AppSettings.getTimelineCount()) {
                    setAdapterItems(new ArrayList<UserInfo>());
                    return true;
//                }
            }

            return super.handleResult(mode, datas);
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

            if (!isContentEmpty())
                showMessage(exception.getMessage());
        }

        public abstract UserInfos getStatusContents(Params params) throws TaskException;

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

}
