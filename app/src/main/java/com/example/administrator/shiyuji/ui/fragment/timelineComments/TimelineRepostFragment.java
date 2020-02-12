package com.example.administrator.shiyuji.ui.fragment.timelineComments;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfos;
import com.example.administrator.shiyuji.ui.fragment.search.user.UserPaging;
import com.example.administrator.shiyuji.ui.fragment.support.TimelinePaging;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.fragment.support.BasicFooterView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * 某条原创微博的转发微博
 *
 */
public class TimelineRepostFragment extends AListFragment<UserInfo, UserInfos, UserInfo> implements ATabsFragment.ITabInitData {

    public static TimelineRepostFragment newInstance(StatusContent statusContent) {
        Bundle args = new Bundle();
        args.putSerializable("status", statusContent);

        TimelineRepostFragment fragment = new TimelineRepostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private StatusContent statusContent;

    private BizFragment bizFragment;

    @Override
    public int inflateContentView() {
        return R.layout.ui_timeline_comment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        statusContent = savedInstanceState == null ? (StatusContent) getArguments().getSerializable("status")
                : (StatusContent) savedInstanceState.getSerializable("status");

        bizFragment = BizFragment.createBizFragment(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("status", statusContent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        BizFragment.createBizFragment(getActivity()).getFabAnimator().attachToListView(getRefreshView(), null, this);
    }

    @Override
    public void requestData(RefreshMode mode) {
        boolean load = true;

        // 如果还没有加载过数据，切且显示的是当前的页面
//        if (getTaskCount(PAGING_TASK_ID) == 0) {
//            load = AisenUtils.checkTabsFragmentCanRequestData(this);
//        }

        if (load)
            new RepostTask(mode).execute();
    }

    @Override
    public void onTabRequestData() {
        requestDataDelay(100);
    }

    class RepostTask extends ATimelineTask {

        public RepostTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        public UserInfos getStatusContents(Params params) throws TaskException {
            String statusId = statusContent.getRow_key();
            params.addParameter("row_key",statusId);

            UserPaging paging = (UserPaging) getPaging();
            params.addParameter("max_id",paging.getLast());

            UserInfos userInfos = SinaSDK.getInstance(getTaskCacheMode(this), AppContext.getAccount().getAccessToken()).getLikeUsers(params);


            Setting action = getSetting("base_url");
            String base_url = action.getValue().split("/")[2].split(":")[0];
            for (UserInfo userInfo :userInfos.getUserInfoList()){
                userInfo.setAvatar(userInfo.getAvatar().replaceAll("localhost",base_url));
            }
//            params.addParameter("id", statusId);
//
//            StatusRepost statusRepost = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).statusRepostTimeline(params);
//            if (statusRepost != null) {
//                for (StatusContent status : statusRepost.getReposts()) {
//                    status.setRetweeted_status(null);
//                }
//
//                return new StatusContents(statusRepost.getReposts());
//            }
//
//            throw new TaskException(TaskException.TaskError.resultIllegal.toString());
            return userInfos;
        }

    }

    class RepostItemView extends ARecycleViewItemView<UserInfo> {

        @ViewInject(id = R.id.imgPhoto)
        ImageView imgPhoto;

        @ViewInject(id = R.id.txtName)
        TextView txtName;

        @ViewInject(id = R.id.gender_img)
        ImageView gender;

        @ViewInject(id = R.id.txtDesc)
        TextView introduce;

        @ViewInject(id = R.id.attention_button)
        Button attention;

        int firstTop;
        int normalTop;
        UserInfo data;
        ABaseFragment fragment;


        public RepostItemView(View convertView, TimelineRepostFragment timelineRepostFragment) {
            super(getActivity(), convertView);

            this.fragment = timelineRepostFragment;
            firstTop = Utils.dip2px(getContext(), 16);
            normalTop = Utils.dip2px(getContext(), 8);
        }


        @Override
        public void onBindData(View convertView, UserInfo data, int position) {
            this.data = data;

            txtName.setText(data.getNickname());
            introduce.setText(data.getIntroduce());
            BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(data), imgPhoto, ImageConfigUtils.getLargePhotoConfig());
            gender.setVisibility(View.GONE);
            attention.setVisibility(View.GONE);

        }

    }



    @Override
    public IItemViewCreator<UserInfo> configItemViewCreator() {
        return new IItemViewCreator<UserInfo>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(R.layout.item_user, parent, false);
            }

            @Override
            public IITemView<UserInfo> newItemView(View convertView, int viewType) {
                return new RepostItemView(convertView,TimelineRepostFragment.this);
            }

        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

//        if (position < getAdapterItems().size())
//            TimelineDetailPagerFragment.launch(getActivity(), getAdapterItems().get(position));
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
                return new BasicFooterView<UserInfo>(getActivity(), convertView, TimelineRepostFragment.this) {

                    @Override
                    protected String endpagingText() {
                        return getString(R.string.disable_status);
                    }

                    @Override
                    protected String loadingText() {
                        return String.format(getString(R.string.loading_status), 10);
                    }

                };
            }

        };
    }

    abstract public class ATimelineTask extends APagingTask<Void, Void, UserInfos> {

        public ATimelineTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<UserInfo> parseResult(UserInfos userInfos) {
            return userInfos.getUserInfoList();
        }

        @Override
        protected UserInfos workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {
            Params params = new Params();

            if (mode == APagingFragment.RefreshMode.refresh && !TextUtils.isEmpty(previousPage))
                params.addParameter("since_id", "0");

            if (mode == APagingFragment.RefreshMode.update && !TextUtils.isEmpty(nextPage))
                params.addParameter("max_id", nextPage);

            params.addParameter("count", String.valueOf(AppSettings.getTimelineCount()));

            return getStatusContents(params);
        }

        @Override
        protected boolean handleResult(RefreshMode mode, List<UserInfo> datas) {
            // 如果是重置或者刷新数据，加载数据大于分页大小，则清空之前的数据
            if (mode == RefreshMode.refresh) {
                // 目前微博加载分页大小是默认大小
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

        @Override
        protected void onFinished() {
            super.onFinished();

//            if (getActivity() != null) {
//                Fragment fragment = getActivity().getFragmentManager().findFragmentByTag(SinaCommonActivity.FRAGMENT_TAG);
//                if (fragment != null && fragment instanceof TimelineDetailPagerFragment) {
//                    ((TimelineDetailPagerFragment) fragment).refreshEnd();
//                }
//            }
        }

        public abstract UserInfos getStatusContents(Params params) throws TaskException;

    }

    @Override
    public boolean onToolbarDoubleClick() {
//        if (AisenUtils.checkTabsFragmentCanRequestData(this)) {
//            Fragment fragment = getActivity().getFragmentManager().findFragmentByTag(SinaCommonActivity.FRAGMENT_TAG);
//            if (fragment != null && fragment instanceof TimelineDetailPagerFragment) {
//                ((TimelineDetailPagerFragment) fragment).swipeRefreshLayout.setRefreshing(true);
//            }
//
//            requestDataDelaySetRefreshing(AppSettings.REQUEST_DATA_DELAY);
//            getRefreshView().setSelectionFromTop(0, 0);
//
//            return true;
//        }

        return false;
    }

}
