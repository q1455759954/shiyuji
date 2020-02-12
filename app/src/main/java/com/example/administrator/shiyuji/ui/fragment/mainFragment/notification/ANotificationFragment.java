package com.example.administrator.shiyuji.ui.fragment.mainFragment.notification;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.shiyuji.ui.fragment.adapter.BasicRecycleViewAdapter;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.base.ARecycleViewSwipeRefreshFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.support.ANotificationHeaderView;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.support.NotificationItemView;
import com.example.administrator.shiyuji.ui.fragment.support.AFooterItemView;
import com.example.administrator.shiyuji.ui.fragment.support.AHeaderItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IPagingAdapter;
import com.example.administrator.shiyuji.ui.fragment.timeline.ATimelineFragment;
import com.example.administrator.shiyuji.ui.fragment.timeline.ATimelineHeaderView;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineItemView;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.util.ArrayList;
import java.util.List;

/**
 *通知页面基类
 * Created by Administrator on 2019/8/8.
 */

public class ANotificationFragment extends ARecycleViewSwipeRefreshFragment<StatusContent, StatusContents, StatusContent> {

    private String feature = "0";
    /**
     * 通知页面上方的内容（@我的，评论，赞）
     * @return
     */
    @Override
    protected AHeaderItemViewCreator<StatusContent> configHeaderViewCreator() {
        return new AHeaderItemViewCreator<StatusContent>() {
            @Override
            public int[][] setHeaders() {
                return new int[][]{ { ANotificationHeaderView.LAYOUT_RES, 100 } };
            }

            @Override
            public IITemView<StatusContent> newItemView(View convertView, int viewType) {
                return new ANotificationHeaderView(ANotificationFragment.this,convertView) {
                    @Override
                    protected int getTitleArrRes() {
                        return 0;
                    }

                    @Override
                    protected String[] getTitleFeature() {
                        return new String[0];
                    }
                };
            }
        };
    }

    /**
     * 重写一个空的添加底部方法，（没有底部）
     * @param footerItemView
     */
    @Override
    protected void addFooterViewToRefreshView(AFooterItemView<?> footerItemView) {
    }
    /**
     * 通知列表
     * @return
     */
    @Override
    public IItemViewCreator<StatusContent> configItemViewCreator() {

        return new IItemViewCreator<StatusContent>() {
            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(NotificationItemView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<StatusContent> newItemView(View convertView, int viewType) {
                return new NotificationItemView(convertView,ANotificationFragment.this);
            }
        };
    }

    @Override
    protected IPagingAdapter<StatusContent> newAdapter(ArrayList<StatusContent> datas) {
        return new NotificationAdapter(configItemViewCreator(), datas);
    }

    @Override
    public void requestData(RefreshMode var1) {

    }

    class NotificationAdapter extends BasicRecycleViewAdapter<StatusContent> {

        public NotificationAdapter(IItemViewCreator<StatusContent> itemViewCreator, ArrayList<StatusContent> datas) {
            super(getActivity(), ANotificationFragment.this, itemViewCreator, datas);
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

    abstract public class ANotificationTask extends APagingTask<Void, Void, StatusContents> {

        public ANotificationTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<StatusContent> parseResult(StatusContents statusContents) {
            return statusContents.getStatuses();
        }

        @Override
        protected StatusContents workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {

            Params params = new Params();
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

            StatusContents result = getStatusContents(params);


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


    public String getFeature() {
        return feature;
    }
}
