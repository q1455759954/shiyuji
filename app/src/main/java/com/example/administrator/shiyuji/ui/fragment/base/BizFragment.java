package com.example.administrator.shiyuji.ui.fragment.base;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.example.administrator.shiyuji.support.action.DoAttendAction;
import com.example.administrator.shiyuji.support.action.DoLikeAction;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.picture.PicsActivity;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineItemView;
import com.example.administrator.shiyuji.util.common.AisenUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/1.
 */

public class BizFragment  extends ABaseFragment{



    /******************以下是关注的逻辑***************/
    private Map<String, WeakReference<DoAttendAction>> attendActionMap = new HashMap<>();

    /**
     * 关注或取消关注
     * @param u
     * @param attend
     * @param v
     * @param callback
     */
    public void doAttend(UserInfo u, boolean attend, View v, DoAttendAction.OnAttendCallback callback) {
        String key = String.valueOf(u.getId());
        DoAttendAction action = attendActionMap.containsKey(key) ? attendActionMap.get(key).get() : null;
        //这个是防止执行worktast时再次点击点赞，onFinish是tast置为null
        if (action != null && action.isRunning())
            return;

        action = new DoAttendAction(getActivity(), this, v, u, attend, callback);
        attendActionMap.put(key, new WeakReference<>(action));
        action.run();

    }

    /*******************************************/



    /******************以下是点赞的逻辑***************/

    private Map<String, WeakReference<DoLikeAction>> likeActionMap = new HashMap<>();
    /**
     * 点赞或者取消点赞
     *
     */
    public void doLike(final ILikeBean data, final boolean like, View likeView, final DoLikeAction.OnLikeCallback callback) {
        String key = data.getLikeId();
        DoLikeAction action = likeActionMap.containsKey(key) ? likeActionMap.get(key).get() : null;
        //这个是防止执行worktast时再次点击点赞，onFinish是tast置为null
        if (action != null && action.isRunning())
            return;

        action = new DoLikeAction(getActivity(), this, likeView, data, like, callback);
        likeActionMap.put(key, new WeakReference<>(action));
        action.run();
    }

    public interface ILikeBean {

        String getLikeId();

    }

    public void animScale(final View likeView) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.start();
        likeView.startAnimation(scaleAnimation);
        likeView.postDelayed(new Runnable() {

            @Override
            public void run() {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                likeView.startAnimation(scaleAnimation);
            }

        }, 200);
    }
    /*******************************************/


    private Activity mActivity;
    // XXX /*查看用户详情*/
    View.OnClickListener UserShowListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final UserInfo user = (UserInfo) v.getTag();

//            launchProfile(user);
        }
    };

    public static BizFragment createBizFragment(ABaseFragment fragment) {
        try {
            if (fragment != null && fragment.getActivity() != null) {
                BizFragment bizFragment = (BizFragment) fragment.getActivity().getFragmentManager().findFragmentByTag("org.aisen.android.ui.BizFragment");

                if (bizFragment == null) {
                    bizFragment = new BizFragment();
                    bizFragment.mActivity = fragment.getActivity();
                    fragment.getActivity().getFragmentManager().beginTransaction().add(bizFragment, "org.aisen.android.ui.BizFragment").commit();
                }

                return bizFragment;
            }
        } catch (IllegalStateException e) {

        }

        return null;
    }

    public static BizFragment createBizFragment(Activity activity) {
        BizFragment bizFragment = (BizFragment) activity.getFragmentManager().findFragmentByTag("BizFragment");
        if (bizFragment == null) {
            bizFragment = new BizFragment();
            bizFragment.mActivity = activity;

            if (activity instanceof BaseActivity) {
                if (((BaseActivity) activity).isDestory()) {
                    return bizFragment;
                }
            }

            activity.getFragmentManager().beginTransaction().add(bizFragment, "BizFragment").commit();
        }
        return bizFragment;
    }

    private Activity getRealActivity() {
        if (getActivity() != null)
            return getActivity();

        return mActivity;
    }
    /**
     * 大图监听器
     */
    View.OnClickListener PreviousArrOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object[] tag = (Object[]) v.getTag();
            StatusContent bean = (StatusContent) tag[0];
            int selectedIndex = Integer.parseInt(tag[1].toString());

            PicsActivity.launch(getRealActivity(), bean, selectedIndex);
            //activity加载动画效果
            AisenUtils.changeOpenActivityStyle(getActivity());


        }
    };
    /**
     * 设置图片的大图监听器
     * @param view
     * @param bean
     * @param selectedIndex
     */
    public void previousPics(View view, StatusContent bean, int selectedIndex) {
        Object[] tag = new Object[] { bean, selectedIndex };
        view.setTag(tag);
        view.setOnClickListener(PreviousArrOnClickListener);
    }


    public void userShow(View view, UserInfo user) {
        view.setTag(user);
        view.setOnClickListener(UserShowListener);
    }

    @Override
    public int inflateContentView() {
        return -1;
    }
}
