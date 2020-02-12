package com.example.administrator.shiyuji.ui.fragment.timeline;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.support.setting.SettingExtra;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.ui.fragment.adapter.BasicRecycleViewAdapter;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.base.ARecycleViewSwipeRefreshFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.ui.fragment.support.AHeaderItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.BasicFooterView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;
import com.example.administrator.shiyuji.ui.fragment.support.IPagingAdapter;
import com.example.administrator.shiyuji.ui.fragment.support.TimelinePaging;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineDetailPagerFragment;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * 微博列表基类
 *
 */
public abstract class ATimelineFragment extends ARecycleViewSwipeRefreshFragment<StatusContent, StatusContents, StatusContent> {

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
    protected AHeaderItemViewCreator<StatusContent> configHeaderViewCreator() {
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
    public IItemViewCreator<StatusContent> configItemViewCreator() {
        return new IItemViewCreator<StatusContent>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {

                return inflater.inflate(TimelineItemView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<StatusContent> newItemView(View convertView, int viewType) {

                return new TimelineItemView(convertView, ATimelineFragment.this);
            }

        };
    }


    @Override
    protected IPagingAdapter<StatusContent> newAdapter(ArrayList<StatusContent> datas) {
        return new TimelineAdapter(configItemViewCreator(), datas);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        TimelineDetailPagerFragment.launch(getActivity(), getAdapterItems().get(position));
    }

    @Override
    protected IPaging<StatusContent, StatusContents> newPaging() {
        return new TimelinePaging();
    }

    @Override
    protected IItemViewCreator<StatusContent> configFooterViewCreator() {
        return new IItemViewCreator<StatusContent>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(BasicFooterView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<StatusContent> newItemView(View convertView, int viewType) {
                return new BasicFooterView<StatusContent>(getActivity(), convertView, ATimelineFragment.this) {

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

    class TimelineAdapter extends BasicRecycleViewAdapter<StatusContent> {

        public TimelineAdapter(IItemViewCreator<StatusContent> itemViewCreator, ArrayList<StatusContent> datas) {
            super(getActivity(), ATimelineFragment.this, itemViewCreator, datas);
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

    abstract public class ATimelineTask extends APagingTask<Void, Void, StatusContents> {

        public ATimelineTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<StatusContent> parseResult(StatusContents statusContents) {
            return statusContents.getStatuses();
        }

        @Override
        protected StatusContents workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {

            Params params = new Params();

            params.addParameter("account", "17865578373");
            params.addParameter("aa", String.valueOf(System.currentTimeMillis()));
            //如果是更新数据，将第一条数据的id记下来
            if (mode == APagingFragment.RefreshMode.refresh && !TextUtils.isEmpty(previousPage))
                params.addParameter("since_id", nextPage);
            //刷新数据，记下最后一条数据
            if (mode == APagingFragment.RefreshMode.update && !TextUtils.isEmpty(nextPage))
                params.addParameter("max_id", nextPage);

            if (params.getParameter("since_id")!=null){
                Log.d("看","previousPage为："+previousPage);
            }else if (params.getParameter("max_id")!=null){
                Log.d("看","nextPage为："+nextPage);
            }
            params.addParameter("count", String.valueOf(AppSettings.getTimelineCount()));//数量

            StatusContents result = getStatusContents(params);

            Setting action = getSetting("base_url");
            String base_url = action.getValue().split("/")[2].split(":")[0];
            for (StatusContent statusContent : result.getStatuses()){
                if (statusContent.getPic_urls()!=null){
                    for (PicUrls picUrls : statusContent.getPic_urls()){
                        picUrls.setThumbnail_pic(picUrls.getThumbnail_pic().replaceAll("localhost",base_url));
                    }
                }
                if (statusContent.getRetweeted_status()!=null){
                    statusContent.getRetweeted_status().getUserInfo().setAvatar(statusContent.getRetweeted_status().getUserInfo().getAvatar().replaceAll("localhost",base_url));
                    if (statusContent.getRetweeted_status().getPic_urls()!=null){
                        for (PicUrls picUrls : statusContent.getRetweeted_status().getPic_urls()){
                            picUrls.setThumbnail_pic(picUrls.getThumbnail_pic().replaceAll("localhost",base_url));
                        }
                    }
                }
                if (statusContent.getVideoInfo()!=null){
                    statusContent.getVideoInfo().setPic_url(statusContent.getVideoInfo().getPic_url().replaceAll("localhost",base_url));
                    statusContent.getVideoInfo().setVideo_url(statusContent.getVideoInfo().getVideo_url().replaceAll("localhost",base_url));
                }
                statusContent.getUserInfo().setAvatar(statusContent.getUserInfo().getAvatar().replaceAll("localhost",base_url));
            }
//            for (StatusContent statusContent : result.getStatuses()){
//            }

            return result;
        }

        @Override
        protected boolean handleResult(RefreshMode mode, List<StatusContent> datas) {
            // 如果是重置或者刷新数据，加载数据大于分页大小，则清空之前的数据
            if (mode == RefreshMode.refresh) {
//                 目前微博加载分页大小是默认大小
                if (datas.size() >= AppSettings.getTimelineCount()) {
                    setAdapterItems(new ArrayList<StatusContent>());
                    return true;
                }
            }

            return super.handleResult(mode, datas);
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

            if (!isContentEmpty())
                showMessage(exception.getMessage());
        }

        public abstract StatusContents getStatusContents(Params params) throws TaskException;

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

