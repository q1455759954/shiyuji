package com.example.administrator.shiyuji.ui.fragment.mainFragment.life.item;

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
import com.example.administrator.shiyuji.ui.fragment.bean.Commodity;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.detail.AppointmentDetailFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item.AAppointmentFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item.AppointmentItemView;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.detail.LifeDetailFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.detail.WorkDetailFragment;
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
 * Created by Administrator on 2019/10/18.
 */

public abstract class ALifeFragment  extends ARecycleViewSwipeRefreshFragment<LifeInfo, LifeInfos, LifeInfo> {

    private String feature = "0";

    protected int LAYOUT_RES ;



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
    protected AHeaderItemViewCreator<LifeInfo> configHeaderViewCreator() {
        return super.configHeaderViewCreator();
    }



    @Override
    protected IPagingAdapter<LifeInfo> newAdapter(ArrayList<LifeInfo> datas) {
        return new LifeAdapter(configItemViewCreator(), datas);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        if ( LAYOUT_RES == R.layout.item_life_commodity){
            LifeDetailFragment.launch(getActivity(), getAdapterItems().get(position));
        }else {
            WorkDetailFragment.launch(getActivity(), getAdapterItems().get(position));
        }

    }

    @Override
    protected IPaging<LifeInfo, LifeInfos> newPaging() {
        return new LifePaging();
    }

    @Override
    protected IItemViewCreator<LifeInfo> configFooterViewCreator() {
        return new IItemViewCreator<LifeInfo>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(BasicFooterView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<LifeInfo> newItemView(View convertView, int viewType) {
                return new BasicFooterView<LifeInfo>(getActivity(), convertView, ALifeFragment.this) {

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

    class LifeAdapter extends BasicRecycleViewAdapter<LifeInfo> {

        public LifeAdapter(IItemViewCreator<LifeInfo> itemViewCreator, ArrayList<LifeInfo> datas) {
            super(getActivity(), ALifeFragment.this, itemViewCreator, datas);
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

    abstract public class ALifeTask extends APagingTask<Void, Void, LifeInfos> {

        public ALifeTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<LifeInfo> parseResult(LifeInfos lifeInfos) {
            return lifeInfos.getLifeInfos();

        }

        @Override
        protected LifeInfos workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {

            Params params = new Params();

            //如果是更新数据，将第一条数据的id记下来
            if (mode == APagingFragment.RefreshMode.refresh && !TextUtils.isEmpty(previousPage))
                params.addParameter("row_key", "0");
            //刷新数据，记下最后一条数据
            if (mode == APagingFragment.RefreshMode.update && !TextUtils.isEmpty(nextPage))
                params.addParameter("row_key", nextPage);

            params.addParameter("count", String.valueOf(AppSettings.getTimelineCount()));//数量

            LifeInfos result = getStatusContents(params);

            Setting action = getSetting("base_url");
            String base_url = action.getValue().split("/")[2].split(":")[0];

            for (LifeInfo lifeInfo : result.getLifeInfos()){
                if (lifeInfo.getCommodity()!=null){
                    lifeInfo.getCommodity().getUserInfo().setAvatar(lifeInfo.getCommodity().getUserInfo().getAvatar().replaceAll("localhost",base_url));
                    for (PicUrls picUrls : lifeInfo.getCommodity().getPicUrls()){
                        picUrls.setThumbnail_pic(picUrls.getThumbnail_pic().replaceAll("localhost",base_url));
                    }
                }
                if (lifeInfo.getWorkInfo()!=null){
                    lifeInfo.getWorkInfo().getUserInfo().setAvatar(lifeInfo.getWorkInfo().getUserInfo().getAvatar().replaceAll("localhost",base_url));
                }

            }



            return result;
        }

        @Override
        protected boolean handleResult(RefreshMode mode, List<LifeInfo> datas) {
            // 如果是重置或者刷新数据，加载数据大于分页大小，则清空之前的数据
            if (mode == RefreshMode.refresh) {
//                 目前微博加载分页大小是默认大小
//                if (datas.size() >= AppSettings.getTimelineCount()) {
                    setAdapterItems(new ArrayList<LifeInfo>());
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

        public abstract LifeInfos  getStatusContents(Params params) throws TaskException;

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