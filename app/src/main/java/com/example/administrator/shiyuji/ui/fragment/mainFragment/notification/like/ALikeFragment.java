package com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.like;

import android.app.Notification;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.fragment.adapter.BasicRecycleViewAdapter;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.base.ARecycleViewSwipeRefreshFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.NotificationInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.NotificationInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
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

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * Created by Administrator on 2019/9/27.
 */

public abstract class ALikeFragment  extends ARecycleViewSwipeRefreshFragment<NotificationInfo, NotificationInfos, NotificationInfo> {

    private String feature = "0";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        if (getActivity() instanceof AppointmentFragment) {
//            BizFragment.createBizFragment(getActivity()).getFabAnimator().attachToRecyclerView(getRefreshView(), null, null);
//        }

        BaseActivity activity = (BaseActivity) getActivity();

        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("赞");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    protected AHeaderItemViewCreator<NotificationInfo> configHeaderViewCreator() {

        return super.configHeaderViewCreator();
    }

    @Override
    public IItemViewCreator<NotificationInfo> configItemViewCreator() {
        return new IItemViewCreator<NotificationInfo>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {

                return inflater.inflate(LikeItemView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<NotificationInfo> newItemView(View convertView, int viewType) {

                return new LikeItemView(convertView, ALikeFragment.this);
            }

        };
    }


    @Override
    protected IPagingAdapter<NotificationInfo> newAdapter(ArrayList<NotificationInfo> datas) {
        return new LikeAdapter(configItemViewCreator(), datas);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

//        TimelineDetailPagerFragment.launch(getActivity(), getAdapterItems().get(position));
    }

    @Override
    protected IPaging<NotificationInfo, NotificationInfos> newPaging() {
        return new LikePaging();
    }

    @Override
    protected IItemViewCreator<NotificationInfo> configFooterViewCreator() {
        return new IItemViewCreator<NotificationInfo>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(BasicFooterView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<NotificationInfo> newItemView(View convertView, int viewType) {
                return new BasicFooterView<NotificationInfo>(getActivity(), convertView, ALikeFragment.this) {

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

    class LikeAdapter extends BasicRecycleViewAdapter<NotificationInfo> {

        public LikeAdapter(IItemViewCreator<NotificationInfo> itemViewCreator, ArrayList<NotificationInfo> datas) {
            super(getActivity(), ALikeFragment.this, itemViewCreator, datas);
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

    abstract public class ALikeTask extends APagingTask<Void, Void, NotificationInfos> {

        public ALikeTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<NotificationInfo> parseResult(NotificationInfos notificationInfos) {
            return notificationInfos.getNotificationList();
        }

        @Override
        protected NotificationInfos workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {

            Params params = new Params();

            params.addParameter("account", "17865578373");
            params.addParameter("aa", String.valueOf(System.currentTimeMillis()));
            //如果是更新数据，将第一条数据的id记下来
            if (mode == APagingFragment.RefreshMode.refresh && !TextUtils.isEmpty(previousPage))
                params.addParameter("since_id", previousPage);
            //刷新数据，记下最后一条数据
            if (mode == APagingFragment.RefreshMode.update && !TextUtils.isEmpty(nextPage))
                params.addParameter("max_id", nextPage);

            if (params.getParameter("since_id")!=null){
                Log.d("看","previousPage为："+previousPage);
            }else if (params.getParameter("max_id")!=null){
                Log.d("看","nextPage为："+nextPage);
            }
            params.addParameter("count", String.valueOf(AppSettings.getTimelineCount()));//数量

            NotificationInfos result = getStatusContents(params);
            Setting action = getSetting("base_url");
            String base_url = action.getValue().split("/")[2].split(":")[0];
            for (NotificationInfo notificationInfo : result.getNotificationList()){
                notificationInfo.setUserInfo(AppContext.getAccount().getUserInfo());
                notificationInfo.getTargetUser().setAvatar(notificationInfo.getTargetUser().getAvatar().replaceAll("localhost",base_url));
            }

            return result;
        }

        @Override
        protected boolean handleResult(RefreshMode mode, List<NotificationInfo> datas) {
            // 如果是重置或者刷新数据，加载数据大于分页大小，则清空之前的数据
            if (mode == RefreshMode.refresh) {
//                 目前微博加载分页大小是默认大小
                if (datas.size() >= AppSettings.getTimelineCount()) {
                    setAdapterItems(new ArrayList<NotificationInfo>());
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

        public abstract NotificationInfos getStatusContents(Params params) throws TaskException;

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
