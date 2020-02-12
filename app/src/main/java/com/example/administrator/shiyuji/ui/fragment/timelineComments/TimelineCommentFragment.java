package com.example.administrator.shiyuji.ui.fragment.timelineComments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.support.action.DeleteAction;
import com.example.administrator.shiyuji.support.action.IAction;
import com.example.administrator.shiyuji.support.permissions.SdcardPermissionAction;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.publish.WorkPublish;
import com.example.administrator.shiyuji.ui.fragment.publish.comment.CustomDialog;
import com.example.administrator.shiyuji.ui.fragment.report.ReportFragment;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComments;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.support.BasicFooterView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 *  微博详情下的评论
 *
 */
public class TimelineCommentFragment extends AListFragment<StatusComment, StatusComments, StatusComment> {

    public static TimelineCommentFragment newInstance(StatusContent status) {
        Bundle arts = new Bundle();
        arts.putSerializable("status", status);

        TimelineCommentFragment fragment = new TimelineCommentFragment();
        fragment.setArguments(arts);
        return fragment;
    }

    StatusContent mStatusContent;

    @Override
    public int inflateContentView() {
        return R.layout.ui_timeline_comment;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);

        getContentView().setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStatusContent = savedInstanceState != null ? (StatusContent) savedInstanceState.getSerializable("status")
                : (StatusContent) getArguments().getSerializable("status");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        BizFragment.createBizFragment(getActivity()).getFabAnimator().attachToListView(getRefreshView(), null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("status", mStatusContent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        if (getAdapterItems().size() == 0 || position >= getAdapterItems().size()) {
            return;
        }

        final StatusComment comment = getAdapterItems().get(position);

        showTypeDialog(comment);

//        if (mStatusContent != null)
//            comment.setStatus(mStatusContent);

//        final List<String> menuList = new ArrayList<String>();
//        // 回复
//        if (comment.getUser() != null && !comment.getUser().getId().equals(AppContext.getAccount().getUser().getId()))
//            menuList.add(commentMenuArr[3]);
//        // 转发
//        if (comment.getStatus() != null &&
//                (comment.getUser() != null && !comment.getUser().getIdstr().equals(AppContext.getAccount().getUser().getIdstr())))
//            menuList.add(commentMenuArr[1]);
//        // 复制
//        menuList.add(commentMenuArr[0]);
//        // 删除
//        if (comment.getUser() != null && AppContext.getAccount().getUser().getIdstr().equals(comment.getUser().getIdstr()))
//            menuList.add(commentMenuArr[2]);

//        new MaterialDialog.Builder(getActivity())
//                .title(comment.getUser().getScreen_name())
//                .items(menuList.toArray(new String[0]))
//                .itemsCallback(new MaterialDialog.ListCallback() {
//
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                        AisenUtils.commentMenuSelected(TimelineCommentFragment.this, menuList.toArray(new String[0])[position], comment);
//                    }
//
//                })
//                .show();
    }

    private void showTypeDialog(final StatusComment comment) {
        new IAction(getActivity(), new SdcardPermissionAction(((BaseActivity) getActivity()), null)) {

            @Override
            public void doAction() {
                int id;
                if (comment.getUserInfo().getId()==AppContext.getAccount().getUserInfo().getId()){
                    id = R.array.response_type_;
                }else {
                    id = R.array.response_type;
                }
                new MaterialDialog.Builder(getActivity())
                        .items(id)
                        .itemsCallback(new MaterialDialog.ListCallback() {

                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which) {
                                    // 回复
                                    case 0:
                                        String u = String.valueOf(comment.getUserInfo().getId());
                                        CustomDialog mCustomDialog = new CustomDialog(getActivity(),null, R.style.customdialogstyle,comment.getRow_key(),"",null,"response");
                                        mCustomDialog.setOnKeyListener(keylistener);
                                        mCustomDialog.setCancelable(true);
                                        mCustomDialog.show();
                                        break;
                                    // 投诉
                                    case 1:
                                        ReportFragment.launch(TimelineCommentFragment.this.getActivity(),comment.getUserInfo(),comment.getRow_key(),"comment");
                                        break;
                                    //删除
                                    case 2:
                                        Params params = new Params();
                                        params.addParameter("row_key",comment.getRow_key());
                                        params.addParameter("user", String.valueOf(comment.getUserInfo().getId()));
                                        params.addParameter("type","comment");
                                        DeleteAction action = new DeleteAction(getActivity(),params);
                                        action.run();
                                        break;

                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
            }

        }.run();
    }
    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            Log.i("TAG", "键盘code---" + keyCode);
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                return false;
            } else if(keyCode == KeyEvent.KEYCODE_DEL){//删除键
//                if(dialog != null){
//                    mCustomDialog.del();
//                }
                return false;
            }else{
                return true;

            }
        }
    };
    @Override
    protected IItemViewCreator<StatusComment> configFooterViewCreator() {
        return new IItemViewCreator<StatusComment>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(BasicFooterView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<StatusComment> newItemView(View convertView, int viewType) {
                return new BasicFooterView<StatusComment>(getActivity(), convertView, TimelineCommentFragment.this) {

                    @Override
                    protected String endpagingText() {
                        return getString(R.string.disable_comments);
                    }

                    @Override
                    protected String loadingText() {
                        return String.format(getString(R.string.loading_cmts), 10);
                    }

                };
            }

        };
    }

    @Override
    public IItemViewCreator<StatusComment> configItemViewCreator() {
        return new IItemViewCreator<StatusComment>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(TimelineCommentItemView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<StatusComment> newItemView(View convertView, int viewType) {
                return new TimelineCommentItemView(TimelineCommentFragment.this, convertView);
            }

        };
    }

    @Override
    protected IPaging<StatusComment, StatusComments> newPaging() {
        return new CommentPaging();
    }

    @Override
    public void requestData(RefreshMode mode) {
        new CommentTask(mode).execute();
    }

    class CommentTask extends APagingTask<Void, Void, StatusComments> {

        public CommentTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<StatusComment> parseResult(StatusComments statusComments) {
            return statusComments.getComments();
        }

        @Override
        protected boolean handleResult(RefreshMode mode, List<StatusComment> datas) {
            // 如果是重置或者刷新数据，加载数据大于分页大小，则清空之前的数据
            if (mode == RefreshMode.reset || mode == RefreshMode.refresh)
                // 目前微博加载分页大小是默认大小
//                if (datas.size() >= AppSettings.getCommentCount()) {
                    setAdapterItems(new ArrayList<StatusComment>());
                    return true;
//                }

//            return super.handleResult(mode, datas);
        }

        @Override
        protected StatusComments workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {

            String row_key = mStatusContent.getRow_key();
            Params params = new Params();

            params.addParameter("row_key",row_key);

            //如果是更新数据，将第一条数据的id记下来
            if (mode == APagingFragment.RefreshMode.refresh && !TextUtils.isEmpty(previousPage))
                params.addParameter("startRowKey", "0");
            //刷新数据，记下最后一条数据
            if (mode == APagingFragment.RefreshMode.update && !TextUtils.isEmpty(nextPage))
                params.addParameter("startRowKey", nextPage);

            StatusComments s = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).commentsHotShow(params);


            Setting action = getSetting("base_url");
            String base_url = action.getValue().split("/")[2].split(":")[0];
            for (StatusComment statusComment : s.getComments()){
                statusComment.getUserInfo().setAvatar(statusComment.getUserInfo().getAvatar().replaceAll("localhost",base_url));
            }
            return s;
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

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
