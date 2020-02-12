package com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.fragment.adapter.BasicRecycleViewAdapter;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.base.ARecycleViewSwipeRefreshFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfos;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.detail.AppointmentDetailFragment;
import com.example.administrator.shiyuji.ui.fragment.support.AHeaderItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.BasicFooterView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;
import com.example.administrator.shiyuji.ui.fragment.support.IPagingAdapter;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * Created by Administrator on 2019/10/8.
 */

public abstract class AAppointmentFragment extends ARecycleViewSwipeRefreshFragment<AppointmentInfo, AppointmentInfos, AppointmentInfo> {

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
    protected AHeaderItemViewCreator<AppointmentInfo> configHeaderViewCreator() {
        return super.configHeaderViewCreator();
    }

    @Override
    public IItemViewCreator<AppointmentInfo> configItemViewCreator() {
        return new IItemViewCreator<AppointmentInfo>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {

                return inflater.inflate(AppointmentItemView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<AppointmentInfo> newItemView(View convertView, int viewType) {

                return new AppointmentItemView(convertView, AAppointmentFragment.this);
            }

        };
    }


    @Override
    protected IPagingAdapter<AppointmentInfo> newAdapter(ArrayList<AppointmentInfo> datas) {
        return new AppointmentAdapter(configItemViewCreator(), datas);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        AppointmentDetailFragment.launch(getActivity(), getAdapterItems().get(position));
    }

    @Override
    protected IPaging<AppointmentInfo, AppointmentInfos> newPaging() {
        return new AppointmentPaging();
    }

    @Override
    protected IItemViewCreator<AppointmentInfo> configFooterViewCreator() {
        return new IItemViewCreator<AppointmentInfo>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(BasicFooterView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<AppointmentInfo> newItemView(View convertView, int viewType) {
                return new BasicFooterView<AppointmentInfo>(getActivity(), convertView, AAppointmentFragment.this) {

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

    class AppointmentAdapter extends BasicRecycleViewAdapter<AppointmentInfo> {

        public AppointmentAdapter(IItemViewCreator<AppointmentInfo> itemViewCreator, ArrayList<AppointmentInfo> datas) {
            super(getActivity(), AAppointmentFragment.this, itemViewCreator, datas);
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

    abstract public class AAppointmentTask extends APagingTask<Void, Void, AppointmentInfos> {

        public AAppointmentTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<AppointmentInfo> parseResult(AppointmentInfos appointmentInfos) {
            return appointmentInfos.getAppointmentInfos();

        }

        @Override
        protected AppointmentInfos workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {

            Params params = new Params();

            //如果是更新数据，将第一条数据的id记下来
            if (mode == APagingFragment.RefreshMode.refresh && !TextUtils.isEmpty(previousPage))
                params.addParameter("row_key", "0");
            //刷新数据，记下最后一条数据
            if (mode == APagingFragment.RefreshMode.update && !TextUtils.isEmpty(nextPage))
                params.addParameter("row_key", nextPage);

            params.addParameter("count", String.valueOf(AppSettings.getTimelineCount()));//数量

            AppointmentInfos result = getStatusContents(params);
            Setting action = getSetting("base_url");
            String base_url = action.getValue().split("/")[2].split(":")[0];
            for (AppointmentInfo appointmentInfo : result.getAppointmentInfos()){
                appointmentInfo.getUserInfo().setAvatar(appointmentInfo.getUserInfo().getAvatar().replaceAll("localhost",base_url));
            }

            return result;
        }

        @Override
        protected boolean handleResult(RefreshMode mode, List<AppointmentInfo> datas) {
            // 如果是重置或者刷新数据，加载数据大于分页大小，则清空之前的数据
            if (mode == RefreshMode.refresh) {
//                 目前微博加载分页大小是默认大小
//                if (datas.size() >= AppSettings.getTimelineCount()) {
                    setAdapterItems(new ArrayList<AppointmentInfo>());
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

        public abstract AppointmentInfos getStatusContents(Params params) throws TaskException;

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