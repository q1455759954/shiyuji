package com.example.administrator.shiyuji.support.action;

import android.app.Activity;
import android.view.View;


import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.sdk.cache.Extra;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.support.sqlit.SinaDB;
import com.example.administrator.shiyuji.support.sqlit.extra.FieldUtils;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.util.cache.LruMemoryCache;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;
import com.example.administrator.shiyuji.util.viewutil.ViewUtils;

import java.util.List;

/**
 * Created by wangdan on 15/5/1.
 */
public class DoLikeAction extends IAction {

    public static LruMemoryCache<String, LikeBean> likeCache = new LruMemoryCache<>(60);

    BizFragment bizFragment;
    boolean like;
    BizFragment.ILikeBean data;
    OnLikeCallback callback;
    View likeView;

    public DoLikeAction(Activity context, BizFragment bizFragment, View likeView,
                        BizFragment.ILikeBean data, boolean like, OnLikeCallback callback) {
        super(context, new WebLoginAction(context, bizFragment));

        this.bizFragment = bizFragment;
        this.data = data;
        this.like = like;
        this.likeView = likeView;
        this.callback = callback;
    }

    @Override
    public void doAction() {
        final String key = data.getLikeId();

        LikeBean likeBean = likeCache.get(key);

        // 如果还没有点赞过，首先添加一个点赞
        if (likeBean == null) {
            likeBean = new LikeBean();
            likeBean.setLiked(like);
            likeBean.setStatusId(key);

            LikeDB.insert(likeBean);
            likeCache.put(key, likeBean);
        }
        // 点赞过了，刷新内存，刷新DB
        else {
            likeBean.setLiked(like);

            LikeDB.insert(likeBean);
        }

        // 开始处理点赞
        new LikeTask().execute();

//        UMengUtil.onEvent(bizFragment.getActivity(), "do_like");
    }

    public static void refreshLikeCache() {
        likeCache.evictAll();

        new WorkTask<Void, Void, Void>() {

            @Override
            public Void workInBackground(Void... params) throws TaskException {
                String uid = String.valueOf(AppContext.getAccount().getUserInfo().getId());

                String selection = String.format(" %s = ? ", FieldUtils.OWNER);
                String[] args = new String[]{ uid };
                List<LikeBean> likeBeans = SinaDB.getDB().select(LikeBean.class, selection, args);
                SinaDB.getDB().deleteAll(new Extra(uid, null), LikeBean.class);
                SinaDB.getDB().insert(new Extra(uid, null), likeBeans);

                for (LikeBean likeBean : likeBeans)
                    likeCache.put(likeBean.getStatusId(), likeBean);
                return null;
            }

        }.execute();
    }

    public interface OnLikeCallback {

        // 点赞失败或者成功后，有必要时会回调这个方法刷新UI
        void onLikeFaild(BizFragment.ILikeBean data);

        void onLikeSuccess(BizFragment.ILikeBean data, View likeView);

    }

    public boolean isRunning() {
        return mTask != null;
    }

    LikeTask mTask = null;
    class LikeTask extends WorkTask<Void, Void, String> {

        LikeTask() {
            mTask = this;
        }

        @Override
        public String workInBackground(Void... params) throws TaskException {
            String likeResultBean = null;

            if (!data.getLikeId().endsWith("_collect")){
                likeResultBean = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).makeLike(data.getLikeId(), like);
            }else {
                likeResultBean = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).makeCollection(data.getLikeId(), like);
            }

            return likeResultBean;
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

            final String key = String.valueOf(data.getLikeId());
            LikeBean likeBean = likeCache.get(key);
            if (likeBean != null) {
                likeBean.setLiked(!like);
                LikeDB.insert(likeBean);
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

            callback.onLikeFaild(data);
        }

        @Override
        protected void onSuccess(String likeResultBean) {
            super.onSuccess(likeResultBean);
            if (!likeResultBean.equals("ok")){
                callback.onLikeFaild(data);
            }else {
                final String key = String.valueOf(data.getLikeId());
                LikeBean likeBean = likeCache.get(key);
                likeBean.setLiked(like);
                LikeDB.insert(likeBean);

                if (bizFragment.getActivity() == null) {
                    return;
                }

                callback.onLikeSuccess(data, likeView);
            }


        }

        @Override
        protected void onFinished() {
            super.onFinished();

            mTask = null;
        }
    }

}
