package com.example.administrator.shiyuji.support.action;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.sdk.cache.Extra;
import com.example.administrator.shiyuji.support.bean.AccountBean;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.support.sqlit.SinaDB;
import com.example.administrator.shiyuji.support.sqlit.extra.FieldUtils;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.util.accountutil.AccountUtils;
import com.example.administrator.shiyuji.util.cache.LruMemoryCache;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;

import java.util.List;

/**
 * Created by Administrator on 2019/12/26.
 */

public class DeleteAction extends IAction {



    Params params ;
    Context context;
    public DeleteAction(Activity context, Params params) {
        super(context, new WebLoginAction(context, null));

       this.params=params;
    }

    @Override
    public void doAction() {

        // 开始处理删除
        new DeleteTask(params).execute();

//        UMengUtil.onEvent(bizFragment.getActivity(), "do_like");
    }



    public interface OnLikeCallback {

        // 点赞失败或者成功后，有必要时会回调这个方法刷新UI
        void onLikeFaild(BizFragment.ILikeBean data);

        void onLikeSuccess(BizFragment.ILikeBean data, View likeView);

    }

    public boolean isRunning() {
        return mTask != null;
    }

    DeleteTask mTask = null;
    class DeleteTask extends WorkTask<Void, Void, String> {

        Params p;

        DeleteTask(Params params) {
            mTask = this;
            p=params;
        }

        @Override
        public String workInBackground(Void... params) throws TaskException {
            String likeResultBean = null;

            likeResultBean = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).delete(p);


            return likeResultBean;
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

        }

        @Override
        protected void onSuccess(String result) {
            super.onSuccess(result);
            if (result.equals("ok")){
                AccountBean accountBean = AppContext.getAccount();
                accountBean.getUserInfo().setStatus(accountBean.getUserInfo().getStatus()-1);
                AccountUtils.setLogedinAccount(accountBean);
                AppContext.setAccount(accountBean);//保存当前账户
                Toast.makeText(getContext(),"删除成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(),"删除失败",Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        protected void onFinished() {
            super.onFinished();

            mTask = null;
        }
    }

}

