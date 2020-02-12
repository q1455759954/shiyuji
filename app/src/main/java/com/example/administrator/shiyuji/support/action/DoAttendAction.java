package com.example.administrator.shiyuji.support.action;

import android.app.Activity;
import android.view.View;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.sdk.cache.Extra;
import com.example.administrator.shiyuji.support.bean.AccountBean;
import com.example.administrator.shiyuji.support.bean.AttendBean;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.sqlit.AttendDB;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.support.sqlit.SinaDB;
import com.example.administrator.shiyuji.support.sqlit.extra.FieldUtils;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.util.accountutil.AccountUtils;
import com.example.administrator.shiyuji.util.cache.LruMemoryCache;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;

import java.util.List;

/**
 * Created by Administrator on 2019/9/23.
 */

public class DoAttendAction extends IAction{

    public static LruMemoryCache<String, AttendBean> attendCache = new LruMemoryCache<>(60);

    BizFragment bizFragment;
    boolean attend;
    UserInfo data;
    DoAttendAction.OnAttendCallback callback;
    View likeView;

    public DoAttendAction(Activity context, BizFragment bizFragment, View likeView,
                          UserInfo data, boolean attend, OnAttendCallback callback) {
        super(context, new WebLoginAction(context, bizFragment));

        this.bizFragment = bizFragment;
        this.data = data;
        this.attend = attend;
        this.likeView = likeView;
        this.callback = callback;
    }

    @Override
    public void doAction() {
        final String key = String.valueOf(data.getId());

        AttendBean attendBean = attendCache.get(key);

        // 如果还没有关注过，首先添加一个关注
        if (attendBean == null) {
            attendBean = new AttendBean();
            attendBean.setTargetUid(String.valueOf(data.getId()));
            attendBean.setAttend(attend);

            AttendDB.insert(attendBean);
            attendCache.put(key, attendBean);
        }
        // 关注过了，刷新内存，刷新DB
        else {
            attendBean.setAttend(attend);

            AttendDB.insert(attendBean);
        }

        // 开始处理关注
        new DoAttendAction.AttendTask().execute();

//        UMengUtil.onEvent(bizFragment.getActivity(), "do_like");
    }

    public static void refreshLikeCache() {
//        attendCache.evictAll();
//
//        new WorkTask<Void, Void, Void>() {
//
//            @Override
//            public Void workInBackground(Void... params) throws TaskException {
//                String uid = String.valueOf(AppContext.getAccount().getUserInfo().getId());
//
//                String selection = String.format(" %s = ? ", FieldUtils.OWNER);
//                String[] args = new String[]{ uid };
//                List<LikeBean> likeBeans = SinaDB.getDB().select(LikeBean.class, selection, args);
//                SinaDB.getDB().deleteAll(new Extra(uid, null), LikeBean.class);
//                SinaDB.getDB().insert(new Extra(uid, null), likeBeans);
//
//                for (LikeBean likeBean : likeBeans)
//                    attendCache.put(likeBean.getStatusId(), likeBean);
//                return null;
//            }
//
//        }.execute();
    }

    public interface OnAttendCallback {

        // 关注失败或者成功后，有必要时会回调这个方法刷新UI
        void onAttendFaild(UserInfo data);

        void onAttendSuccess(UserInfo data, View likeView);

    }

    public boolean isRunning() {
        return mTask != null;
    }

    DoAttendAction.AttendTask mTask = null;
    class AttendTask extends WorkTask<Void, Void, String> {

        AttendTask() {
            mTask = this;
        }

        @Override
        public String workInBackground(Void... params) throws TaskException {

            int uId = AppContext.getAccount().getUserInfo().getId();
            int targetUser = data.getId();
            Params p = new Params();
            p.addParameter("targetUser", String.valueOf(targetUser));
            if (!attend){
                p.addParameter("type","cancel");
            }else {
                p.addParameter("type","attend");
            }
            return  SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).makeAttends(p);

        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

            final String key = String.valueOf(data.getId());
            AttendBean attendBean = attendCache.get(key);
            if (attendBean != null) {
                attendBean.setAttend(!attend);
                AttendDB.insert(attendBean);
            }

            if (bizFragment.getActivity() == null)
                return;

            // 未登录，或者登录失效
//            if ("-100".equalsIgnoreCase(exception.getCode())) {
//                AppContext.clearCookie();
//
//                run();
//            }
//            else {
//                ViewUtils.showMessage(getContext(), exception.getMessage());
//            }

            callback.onAttendFaild(data);
        }

        @Override
        protected void onSuccess(String likeResultBean) {
            super.onSuccess(likeResultBean);
            if (!likeResultBean.equals("ok")){
                callback.onAttendFaild(data);
            }else {
                final String key = String.valueOf(data.getId());
                AttendBean attendBean = attendCache.get(key);
                attendBean.setAttend(attend);
                AttendDB.insert(attendBean);

                if (bizFragment.getActivity() == null) {
                    return;
                }

                callback.onAttendSuccess(data, likeView);
            }
            AccountBean accountBean = AppContext.getAccount();
            if (!attend){
                accountBean.getUserInfo().setAttends(accountBean.getUserInfo().getAttends()-1);
            }else {
                accountBean.getUserInfo().setAttends(accountBean.getUserInfo().getAttends()+1);
            }
            AccountUtils.setLogedinAccount(accountBean);
            AppContext.setAccount(accountBean);//保存当前账户


        }

        @Override
        protected void onFinished() {
            super.onFinished();

            mTask = null;
        }
    }

}

