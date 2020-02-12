package com.example.administrator.shiyuji.ui.fragment.timelineResponse;

import android.content.DialogInterface;
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
import com.example.administrator.shiyuji.ui.fragment.adapter.BasicRecycleViewAdapter;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.base.ARecycleViewSwipeRefreshFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComments;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.publish.LifeFragmentPublish;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.publish.WorkPublish;
import com.example.administrator.shiyuji.ui.fragment.publish.comment.CustomDialog;
import com.example.administrator.shiyuji.ui.fragment.report.ReportFragment;
import com.example.administrator.shiyuji.ui.fragment.support.AFooterItemView;
import com.example.administrator.shiyuji.ui.fragment.support.AHeaderItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.BasicFooterView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IPaging;
import com.example.administrator.shiyuji.ui.fragment.support.IPagingAdapter;
import com.example.administrator.shiyuji.ui.fragment.timeline.ATimelineFragment;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.CommentPaging;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineCommentFragment;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineDetailPagerFragment;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * 回复页面基类
 * Created by Administrator on 2019/8/30.
 */

public class ATimelineResponsePagerFragment extends ARecycleViewSwipeRefreshFragment<StatusComment, StatusComments, StatusComment> {

    public static StatusComment mStatusComment;

    private String feature = "0";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStatusComment = savedInstanceState != null ? (StatusComment) savedInstanceState.getSerializable("statusComment")
                : (StatusComment) getArguments().getSerializable("statusComment");
    }


    /**
     * 回复页面上方的原评论
     * @return
     */
    @Override
    protected AHeaderItemViewCreator<StatusComment> configHeaderViewCreator() {
        return new AHeaderItemViewCreator<StatusComment>() {
            @Override
            public int[][] setHeaders() {
                return new int[][]{ { ATimelineResponseHeaderView.LAYOUT_RES, 100 } };
            }

            @Override
            public IITemView<StatusComment> newItemView(View convertView, int viewType) {
                return new ATimelineResponseHeaderView(ATimelineResponsePagerFragment.this,convertView,mStatusComment) {
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
     * 回复页面底部（加载）
     * @param
     */
    @Override
    protected IItemViewCreator<StatusComment> configFooterViewCreator() {
        return new IItemViewCreator<StatusComment>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(BasicFooterView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<StatusComment> newItemView(View convertView, int viewType) {
                return new BasicFooterView<StatusComment>(getActivity(), convertView, ATimelineResponsePagerFragment.this) {

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        showTypeDialog();
    }

    private void showTypeDialog() {
        new IAction(getActivity(), new SdcardPermissionAction(((BaseActivity) getActivity()), null)) {

            @Override
            public void doAction() {
                int id;
                if (mStatusComment.getUserInfo().getId()==AppContext.getAccount().getUserInfo().getId()){
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
                                        String pre = "回复"+"<a href='/n/BanneyZ'>"+mStatusComment.getUserInfo().getNickname()+"</a>:";
                                        String u = String.valueOf(mStatusComment.getUserInfo().getId());
                                        CustomDialog mCustomDialog = new CustomDialog(getActivity(),null, R.style.customdialogstyle,mStatusComment.getRow_key(),pre,u,"response");
                                        mCustomDialog.setOnKeyListener(keylistener);
                                        mCustomDialog.setCancelable(true);
                                        mCustomDialog.show();
                                        break;
                                    // 投诉
                                    case 1:
                                        ReportFragment.launch(ATimelineResponsePagerFragment.this.getActivity(),mStatusComment.getUserInfo(),mStatusComment.getRow_key(),"comment");
                                        break;
                                    //删除
                                    case 2:
                                        Params params = new Params();
                                        params.addParameter("row_key",mStatusComment.getRow_key());
                                        params.addParameter("user", String.valueOf(mStatusComment.getUserInfo().getId()));
                                        params.addParameter("type","response");
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

    protected int timelineCount() {
//        return AppSettings.getCommentCount();
        return 8;
    }
    /**
     * 回复列表
     * @return
     */
    @Override
    public IItemViewCreator<StatusComment> configItemViewCreator() {

        return new IItemViewCreator<StatusComment>() {
            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(TimelineResponseItemView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<StatusComment> newItemView(View convertView, int viewType) {
                return new TimelineResponseItemView(convertView, ATimelineResponsePagerFragment.this);
            }
        };
    }

    @Override
    protected IPagingAdapter<StatusComment> newAdapter(ArrayList<StatusComment> datas) {
        return new ATimelineResponsePagerFragment.ResponseAdapter(configItemViewCreator(), datas);
    }

    @Override
    public void requestData(RefreshMode var1) {

    }

    class ResponseAdapter extends BasicRecycleViewAdapter<StatusComment> {

        public ResponseAdapter(IItemViewCreator<StatusComment> itemViewCreator, ArrayList<StatusComment> datas) {
            super(getActivity(), ATimelineResponsePagerFragment.this, itemViewCreator, datas);
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

    @Override
    protected IPaging<StatusComment, StatusComments> newPaging() {
        return new CommentPaging();
    }

    abstract public class AResponseTask extends APagingTask<Void, Void, StatusComments> {

        public AResponseTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<StatusComment> parseResult(StatusComments statusComments) {
            return statusComments.getComments();
        }

        @Override
        protected StatusComments workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {

            String row_key = mStatusComment.getRow_key();


            Params params = new Params();

            //如果是更新数据，将第一条数据的id记下来
            if (mode == APagingFragment.RefreshMode.refresh && !TextUtils.isEmpty(previousPage)){
                params.addParameter("startRowKey", "0");
            }

            //刷新数据，记下最后一条数据
            if (mode == APagingFragment.RefreshMode.update && !TextUtils.isEmpty(nextPage))
                params.addParameter("startRowKey", nextPage);

            params.addParameter("row_key",row_key);
            Setting action = getSetting("base_url");
            String base_url = action.getValue().split("/")[2].split(":")[0];
            StatusComments s = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).getResponseByRowkey(params);
            for (StatusComment statusComment : s.getComments()){
                statusComment.getUserInfo().setAvatar(statusComment.getUserInfo().getAvatar().replaceAll("localhost",base_url));
            }
            return s;

        }

        @Override
        protected boolean handleResult(RefreshMode mode, List<StatusComment> datas) {
            // 如果是重置或者刷新数据，加载数据大于分页大小，则清空之前的数据
            if (mode == RefreshMode.refresh) {
//                 目前微博加载分页大小是默认大小
                setAdapterItems(new ArrayList<StatusComment>());
                return true;

            }

            return super.handleResult(mode, datas);
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

            if (!isContentEmpty())
                showMessage(exception.getMessage());
        }

        public abstract StatusComments getStatusComments(Params params) throws TaskException;

    }


    public String getFeature() {
        return feature;
    }
}
