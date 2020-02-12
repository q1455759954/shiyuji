package com.example.administrator.shiyuji.support.action;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;

/**
 * Created by Administrator on 2019/12/26.
 */

public class TurnAction extends IAction{

    Params params ;
    Context context;
    TurnCallback turnCallback;
    public TurnAction(Activity context,Params params,TurnCallback callback) {
        super(context, new WebLoginAction(context, null));

        this.params=params;
        turnCallback= callback;
    }

    @Override
    public void doAction() {

        // 开始处理删除
        new TurnTask(params).execute();

//        UMengUtil.onEvent(bizFragment.getActivity(), "do_like");
    }

    public interface TurnCallback {

        void onSuccess();

    }

    public interface OnLikeCallback {

        // 点赞失败或者成功后，有必要时会回调这个方法刷新UI
        void onLikeFaild(BizFragment.ILikeBean data);

        void onLikeSuccess(BizFragment.ILikeBean data, View likeView);

    }

    public boolean isRunning() {
        return mTask != null;
    }

    TurnTask mTask = null;
    class TurnTask extends WorkTask<Void, Void, String> {

        Params p;

        TurnTask(Params params) {
            mTask = this;
            p=params;
        }

        @Override
        public String workInBackground(Void... params) throws TaskException {
            String likeResultBean = null;

            likeResultBean = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).soldOut(p);


            return likeResultBean;
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

        }

        @Override
        protected void onSuccess(String result) {
            super.onSuccess(result);
            turnCallback.onSuccess();

        }

        @Override
        protected void onFinished() {
            super.onFinished();

            mTask = null;
        }
    }

}
