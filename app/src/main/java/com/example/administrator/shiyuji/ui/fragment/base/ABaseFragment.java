package com.example.administrator.shiyuji.ui.fragment.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.sdk.base.ABizLogic;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.network.IResult;
import com.example.administrator.shiyuji.util.task.ITaskManager;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.TaskManager;
import com.example.administrator.shiyuji.util.task.WorkTask;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;
import com.example.administrator.shiyuji.util.viewutil.ViewUtils;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapOwner;

public abstract class ABaseFragment extends Fragment implements ITaskManager,BitmapOwner {
    static final String TAG = "AFragment-Base";
    private TaskManager taskManager;
    ViewGroup rootView;
    @ViewInject(
            idStr = "layoutLoading"
    )
    @Nullable
    public View loadingLayout;
    @ViewInject(
            idStr = "layoutLoadFailed"
    )
    @Nullable
    public View loadFailureLayout;
    @ViewInject(
            idStr = "layoutContent"
    )
    @Nullable
    public View contentLayout;
    @ViewInject(
            idStr = "layoutEmpty"
    )
    @Nullable
    public View emptyLayout;
    private boolean contentEmpty = true;
    protected long lastResultGetTime = 0L;
    private boolean destory = false;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
    };
    View.OnClickListener innerOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(v.getId() == R.id.layoutReload) {
                ABaseFragment.this.requestData();
            } else if(v.getId() == R.id.layoutRefresh) {
                ABaseFragment.this.requestData();
            }

        }
    };

    public ABaseFragment() {
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof BaseActivity) {
            ((BaseActivity)activity).addFragment(this.toString(), this);
        }

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.taskManager = new TaskManager();
//        if(savedInstanceState != null) {
//            this.taskManager.restore(savedInstanceState);
//        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(this.inflateContentView() > 0) {
            ViewGroup contentView = (ViewGroup)inflater.inflate(this.inflateContentView(), (ViewGroup)null);
            contentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            this.setupContentView(inflater, contentView, savedInstanceState);
            return this.getContentView();
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    protected void setupContentView(LayoutInflater inflater, ViewGroup contentView, Bundle savedInstanceState) {
        this.setContentView(contentView);
        this._layoutInit(inflater, savedInstanceState);
        this.layoutInit(inflater, savedInstanceState);
    }

    public void setContentView(ViewGroup view) {
        this.rootView = view;
    }

    public ViewGroup getContentView() {
        return this.rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null) {
            this.requestData();
        }

    }

    public boolean onHomeClick() {
        return this.onBackClick();
    }

    public boolean onBackClick() {
        return false;
    }

    public void requestData() {
    }

    /**
     *  设置 获取数据时是否从缓存中取
     * @param task
     * @return
     */
    protected final ABizLogic.CacheMode getTaskCacheMode(WorkTask task) {
        return task != null && TextUtils.isEmpty(task.getTaskId())? ABizLogic.CacheMode.disable:(this.getTaskCount(task.getTaskId()) == 1? ABizLogic.CacheMode.auto: ABizLogic.CacheMode.disable);
    }

    public void requestDataDelay(long delay) {
        Runnable requestDelayRunnable = new Runnable() {
            public void run() {
                ABaseFragment.this.requestData();
            }
        };
        this.runUIRunnable(requestDelayRunnable, delay);
    }

    public void requestDataOutofdate() {
        this.requestData();
    }

    void _layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        InjectUtility.initInjectedView(this.getActivity(), this, this.getContentView());
        View reloadView;
        if(this.getEmptyLayout() != null) {
            reloadView = this.getEmptyLayout().findViewById(R.id.layoutReload);
            if(reloadView != null) {
                this.setViewOnClick(reloadView);
            }
        }

        if(this.getLoadFailureLayout() != null) {
            reloadView = this.getLoadFailureLayout().findViewById(R.id.layoutReload);
            if(reloadView != null) {
                this.setViewOnClick(reloadView);
            }
        }

        this.setViewVisiable(this.getLoadingLayout(), 8);
        this.setViewVisiable(this.getLoadFailureLayout(), 8);
        this.setViewVisiable(this.getEmptyLayout(), 8);
        if(this.isContentEmpty()) {
            if(savedInstanceSate != null) {
                this.requestData();
            } else {
                this.setViewVisiable(this.getEmptyLayout(), 0);
                this.setViewVisiable(this.getContentLayout(), 8);
            }
        } else {
            this.setViewVisiable(this.getContentLayout(), 0);
        }

    }

    public View findViewById(int viewId) {
        return this.getContentView() == null?null:this.getContentView().findViewById(viewId);
    }

    public void setContentEmpty(boolean empty) {
        this.contentEmpty = empty;
    }

    public boolean isContentEmpty() {
        return this.contentEmpty;
    }

    public void setViewVisiable(View v, int visibility) {
        if(v != null && v.getVisibility() != visibility) {
            v.setVisibility(visibility);
        }

    }

    protected void onTaskStateChanged(ABaseFragment.ABaseTaskState state, TaskException exception) {
        if(state == ABaseFragment.ABaseTaskState.prepare) {
            if(this.isContentEmpty()) {
                this.setViewVisiable(this.getLoadingLayout(), 0);
                this.setViewVisiable(this.getContentLayout(), 8);
            } else {
                this.setViewVisiable(this.getLoadingLayout(), 8);
                this.setViewVisiable(this.getContentLayout(), 0);
            }

            this.setViewVisiable(this.getEmptyLayout(), 8);
            if(this.isContentEmpty() && this.getLoadingLayout() == null) {
                this.setViewVisiable(this.getContentLayout(), 0);
            }

            this.setViewVisiable(this.getLoadFailureLayout(), 8);
        } else if(state == ABaseFragment.ABaseTaskState.success) {
            this.setViewVisiable(this.getLoadingLayout(), 8);
            if(this.isContentEmpty()) {
                this.setViewVisiable(this.getEmptyLayout(), 0);
                this.setViewVisiable(this.getContentLayout(), 8);
            } else {
                this.setViewVisiable(this.getContentLayout(), 0);
                this.setViewVisiable(this.getEmptyLayout(), 8);
            }
        } else if(state == ABaseFragment.ABaseTaskState.canceled) {
            if(this.isContentEmpty()) {
                this.setViewVisiable(this.getLoadingLayout(), 8);
                this.setViewVisiable(this.getEmptyLayout(), 0);
            }
        } else if(state == ABaseFragment.ABaseTaskState.falid) {
            if(this.isContentEmpty()) {
                if(this.getLoadFailureLayout() != null) {
                    this.setViewVisiable(this.getLoadFailureLayout(), 0);
                    if(exception != null) {
                        TextView txtLoadFailed = (TextView)this.getLoadFailureLayout().findViewById(R.id.txtLoadFailed);
                        if(txtLoadFailed != null) {
                            txtLoadFailed.setText(exception.getMessage());
                        }
                    }

                    this.setViewVisiable(this.getEmptyLayout(), 8);
                } else {
                    this.setViewVisiable(this.getEmptyLayout(), 0);
                }

                this.setViewVisiable(this.getLoadingLayout(), 8);
            }
        } else if(state == ABaseFragment.ABaseTaskState.finished) {
            ;
        }

    }

    public void showMessage(CharSequence msg) {
        if(!TextUtils.isEmpty(msg) && this.getActivity() != null) {
            ViewUtils.showMessage(this.getActivity(), msg.toString());
        }

    }

    public void showMessage(int msgId) {
        if(this.getActivity() != null) {
            this.showMessage(this.getString(msgId));
        }

    }

    public void onDestroy() {
        this.destory = true;

        try {
            super.onDestroy();
        } catch (Exception var2) {
        }

        this.removeAllTask(true);
//        if(BitmapLoader.getInstance() != null) {
//            BitmapLoader.getInstance().cancelPotentialTask(this);
//        }

    }

    public boolean isDestory() {
        return this.destory;
    }

    public boolean isActivityRunning() {
        return this.getActivity() != null;
    }

    public void onDetach() {
        super.onDetach();
        if(this.getActivity() != null && this.getActivity() instanceof BaseActivity) {
            ((BaseActivity)this.getActivity()).removeFragment(this.toString());
        }

    }

    public final void addTask(WorkTask task) {
        this.taskManager.addTask(task);
    }

    public final void removeTask(String taskId, boolean cancelIfRunning) {
        this.taskManager.removeTask(taskId, cancelIfRunning);
    }

    public final void removeAllTask(boolean cancelIfRunning) {
        this.taskManager.removeAllTask(cancelIfRunning);
    }

    public final int getTaskCount(String taskId) {
        return this.taskManager.getTaskCount(taskId);
    }

//    protected final CacheMode getTaskCacheMode(WorkTask task) {
//        return task != null && TextUtils.isEmpty(task.getTaskId())?CacheMode.disable:(this.getTaskCount(task.getTaskId()) == 1?CacheMode.auto:CacheMode.disable);
//    }

    public void cleatTaskCount(String taskId) {
        this.taskManager.cleatTaskCount(taskId);
    }

    private void setViewOnClick(View v) {
        if(v != null) {
            v.setOnClickListener(this.innerOnClickListener);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(this.taskManager != null) {
            this.taskManager.save(outState);
        }

    }

    protected ITaskManager getTaskManager() {
        return this.taskManager;
    }

    public void runUIRunnable(Runnable runnable) {
        this.runUIRunnable(runnable, 0L);
    }

    public void runUIRunnable(Runnable runnable, long delay) {
        if(delay > 0L) {
            this.mHandler.removeCallbacks(runnable);
            this.mHandler.postDelayed(runnable, delay);
        } else {
            this.mHandler.post(runnable);
        }

    }

    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
    }

    public boolean canDisplay() {
        return true;
    }

    public abstract int inflateContentView();

    public int inflateActivityContentView() {
        return -1;
    }

    public int setActivityTheme() {
        return -1;
    }

    public int configRequestDelay() {
        return 500;
    }

    public View getLoadingLayout() {
        return this.loadingLayout;
    }

    public View getLoadFailureLayout() {
        return this.loadFailureLayout;
    }

    public View getContentLayout() {
        return this.contentLayout;
    }

    public View getEmptyLayout() {
        return this.emptyLayout;
    }

    protected abstract class ABaseTask<Params, Progress, Result> extends WorkTask<Params, Progress, Result> {
        public ABaseTask(String taskId) {
            super(taskId, ABaseFragment.this);
        }

        protected void onPrepare() {
            super.onPrepare();
            ABaseFragment.this.onTaskStateChanged(ABaseFragment.ABaseTaskState.prepare, (TaskException)null);
        }

        protected void onSuccess(Result result) {
            super.onSuccess(result);
            ABaseFragment.this.setContentEmpty(this.resultIsEmpty(result));
            ABaseFragment.this.onTaskStateChanged(ABaseFragment.ABaseTaskState.success, (TaskException)null);
//            if(Logger.DEBUG) {
//                Logger.d("AFragment-Base", "Result获取时间：%s", new Object[]{(new SimpleDateFormat("HH:mm:ss")).format(Long.valueOf(ABaseFragment.this.lastResultGetTime))});
//            }

            if(result instanceof IResult) {
                IResult iResult = (IResult)result;
                if(iResult.fromCache()) {
                    if(iResult.outofdate()) {
                        ABaseFragment.this.runUIRunnable(new Runnable() {
                            public void run() {
//                                Logger.d("AFragment-Base", "数据过期，开始刷新, " + this.toString());
                                ABaseFragment.this.requestDataOutofdate();
                            }
                        }, (long)ABaseFragment.this.configRequestDelay());
                    }
                } else {
                    ABaseFragment.this.lastResultGetTime = System.currentTimeMillis();
                }
            } else {
                ABaseFragment.this.lastResultGetTime = System.currentTimeMillis();
            }

        }

        protected void onFailure(TaskException exception) {
            super.onFailure(exception);
            ABaseFragment.this.onTaskStateChanged(ABaseFragment.ABaseTaskState.falid, exception);
        }

        protected void onCancelled() {
            super.onCancelled();
            ABaseFragment.this.onTaskStateChanged(ABaseFragment.ABaseTaskState.canceled, (TaskException)null);
        }

        protected void onFinished() {
            super.onFinished();
            ABaseFragment.this.onTaskStateChanged(ABaseFragment.ABaseTaskState.finished, (TaskException)null);
        }

        protected boolean resultIsEmpty(Result result) {
            return result == null;
        }
    }

    public static enum ABaseTaskState {
        none,
        prepare,
        falid,
        success,
        finished,
        canceled;

        private ABaseTaskState() {
        }
    }
}
