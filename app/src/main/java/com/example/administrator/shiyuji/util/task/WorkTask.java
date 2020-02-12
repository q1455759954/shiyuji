package com.example.administrator.shiyuji.util.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2019/6/28.
 */


public abstract class WorkTask<Params, Progress, Result> {
    private static final String TAG = "WorkTask";
    private static final int CORE_IMAGE_POOL_SIZE = 10;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final int KEEP_ALIVE = 1;
    private TaskException exception;//异常处理，在doInBackground方法里得到返回的异常信息
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new WorkTask.WorkThread(r, "WorkThread # " + this.mCount.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(3);
    public static final Executor THREAD_POOL_EXECUTOR;
    public static final Executor SERIAL_EXECUTOR;
    private static final int MESSAGE_POST_RESULT = 1;
    private static final int MESSAGE_POST_PROGRESS = 2;
    private static final WorkTask.InternalHandler sHandler;
    private static volatile Executor sDefaultExecutor;
    private final WorkTask.WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;
    private volatile WorkTask.Status mStatus;
    private final AtomicBoolean mTaskInvoked;
    private String taskId;

    public static void init() {
        sHandler.getLooper();
    }

    public final String getTaskId() {
        return this.taskId;
    }

    public WorkTask(String taskId, ITaskManager taskManager) {
        this();
        this.taskId = taskId;
        if(taskManager != null) {
            taskManager.addTask(this);
        }

    }

    public WorkTask() {
        this.mStatus = WorkTask.Status.PENDING;
        this.mTaskInvoked = new AtomicBoolean();
        this.mWorker = new WorkTask.WorkerRunnable() {
            public Result call() throws Exception {
//                Logger.v("WorkTask", (!TextUtils.isEmpty(WorkTask.this.taskId)?WorkTask.this.taskId:"") + " call()");
                WorkTask.this.mTaskInvoked.set(true);
                Process.setThreadPriority(10);
                return WorkTask.this.postResult(WorkTask.this.doInBackground((Params[]) this.mParams));
            }
        };
        this.mFuture = new FutureTask(this.mWorker) {
            protected void done() {
                try {
//                    Logger.v("WorkTask", (!TextUtils.isEmpty(WorkTask.this.taskId)?WorkTask.this.taskId:"") + " done()");
                    Object t = this.get();
//                    Logger.v("WorkTask", (!TextUtils.isEmpty(WorkTask.this.taskId)?WorkTask.this.taskId:"") + " done(), result = " + t);
                    WorkTask.this.postResultIfNotInvoked((Result) t);
                } catch (InterruptedException var2) {
                    Log.w("WorkTask", var2);
                } catch (ExecutionException var3) {
                    throw new RuntimeException("An error occured while executing doInBackground()", var3.getCause());
                } catch (CancellationException var4) {
                    WorkTask.this.postResultIfNotInvoked((Result) null);
                } catch (Throwable var5) {
                    throw new RuntimeException("An error occured while executing doInBackground()", var5);
                }

            }
        };
    }

    private void postResultIfNotInvoked(Result result) {
        boolean wasTaskInvoked = this.mTaskInvoked.get();
//        Logger.v("WorkTask", (!TextUtils.isEmpty(this.taskId)?this.taskId:"") + " postResultIfNotInvoked(), result = " + result + ", wasTaskInvoked = " + wasTaskInvoked);
        if(!wasTaskInvoked) {
            this.postResult(result);
        }

    }

    private Result postResult(Result result) {
        Message message = sHandler.obtainMessage(1, new WorkTask.AsyncTaskResult(this, new Object[]{result}));
        message.sendToTarget();
        return result;
    }

    public final WorkTask.Status getStatus() {
        return this.mStatus;
    }

    protected void onPrepare() {
    }

    protected void onFailure(TaskException exception) {
    }

    protected void onSuccess(Result result) {
    }

    protected Params[] getParams() {
        return this.mWorker.mParams;
    }

    protected void onFinished() {
//        Logger.d("WorkTask", String.format("%s --->onFinished()", new Object[]{TextUtils.isEmpty(this.taskId)?"run ":this.taskId + " run "}));
    }

    public abstract Result workInBackground(Params... var1) throws TaskException;

    private Result doInBackground(Params... params) {
//        Logger.d("WorkTask", String.format("%s --->doInBackground()", new Object[]{TextUtils.isEmpty(this.taskId)?"run ":this.taskId + " run "}));

        try {
            return this.workInBackground(params);
        } catch (TaskException var3) {
            var3.printStackTrace();
            this.exception = var3;
            return null;
        }
    }

    protected final void onPreExecute() {
//        Logger.d("WorkTask", String.format("%s --->onPrepare()", new Object[]{TextUtils.isEmpty(this.taskId)?"run ":this.taskId + " run "}));
        this.onPrepare();
    }

    protected final void onPostExecute(Result result) {
        if(this.exception == null) {
//            Logger.d("WorkTask", String.format("%s --->onSuccess()", new Object[]{TextUtils.isEmpty(this.taskId)?"run ":this.taskId + " run "}));
            this.onSuccess(result);
        } else if(this.exception != null) {
//            Logger.d("WorkTask", String.format("%s --->onFailure(), \nError msg --->", new Object[]{TextUtils.isEmpty(this.taskId)?"run ":this.taskId + " run ", this.exception.getMessage()}));
            this.onFailure(this.exception);
        }

        this.onFinished();
    }

    protected void onProgressUpdate(Progress... values) {
    }

    protected void onCancelled(Result result) {
        this._onCancelled();
    }

    private void _onCancelled() {
        this.onCancelled();
        this.onFinished();
    }

    protected void onCancelled() {
//        Logger.d("WorkTask", String.format("%s --->onCancelled()", new Object[]{TextUtils.isEmpty(this.taskId)?"run ":this.taskId + " run "}));
    }

    public final boolean isCancelled() {
        return this.mFuture.isCancelled();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.mFuture.cancel(mayInterruptIfRunning);
    }

    public final Result get() throws InterruptedException, ExecutionException {
        return this.mFuture.get();
    }

    public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.mFuture.get(timeout, unit);
    }

    public final WorkTask<Params, Progress, Result> executeOnSerialExecutor(Params... params) {
        return this.executeOnExecutor(SERIAL_EXECUTOR, params);
    }

    public final WorkTask<Params, Progress, Result> execute(Params... params) {
        return this.executeOnExecutor(THREAD_POOL_EXECUTOR, params);
    }

    public final WorkTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
        if(this.mStatus != WorkTask.Status.PENDING) {
//            switch(null.$SwitchMap$org$aisen$android$network$task$WorkTask$Status[this.mStatus.ordinal()]) {
//                case 1:
//                    throw new IllegalStateException("Cannot execute task: the task is already running.");
//                case 2:
//                    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
//            }
        }

        this.mStatus = WorkTask.Status.RUNNING;
        if(Looper.myLooper() == Looper.getMainLooper()) {
            this.onPreExecute();
        } else {
            sHandler.post(new Runnable() {
                public void run() {
                    WorkTask.this.onPreExecute();
                }
            });
        }

        this.mWorker.mParams = params;
        exec.execute(this.mFuture);
        return this;
    }

    public static void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }

    protected final void publishProgress(Progress... values) {
        if(!this.isCancelled()) {
            sHandler.obtainMessage(2, new WorkTask.AsyncTaskResult(this, values)).sendToTarget();
        }

    }

    private void finish(Result result) {
        if(this.isCancelled()) {
            this.onCancelled(result);
        } else {
            this.onPostExecute(result);
        }

        this.mStatus = WorkTask.Status.FINISHED;
    }

    static {
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(5, 128, 1L, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        ((ThreadPoolExecutor)THREAD_POOL_EXECUTOR).allowCoreThreadTimeOut(true);
        SERIAL_EXECUTOR = new WorkTask.SerialExecutor();
        sHandler = new WorkTask.InternalHandler();
        sDefaultExecutor = SERIAL_EXECUTOR;
    }

    private static class AsyncTaskResult<Data> {
        final WorkTask mTask;
        final Data[] mData;

        AsyncTaskResult(WorkTask task, Data... data) {
            this.mTask = task;
            this.mData = data;
        }
    }

    private abstract static class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;

        private WorkerRunnable() {
        }
    }

    private static class InternalHandler extends Handler {
        InternalHandler() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message msg) {
            WorkTask.AsyncTaskResult result = (WorkTask.AsyncTaskResult)msg.obj;
            switch(msg.what) {
                case 1:
                    result.mTask.finish(result.mData[0]);
                    break;
                case 2:
                    result.mTask.onProgressUpdate(result.mData);
            }

        }
    }

    public static enum Status {
        PENDING,
        RUNNING,
        FINISHED;

        private Status() {
        }
    }

    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks;
        Runnable mActive;

        private SerialExecutor() {
            this.mTasks = new ArrayDeque();
        }

        public synchronized void execute(final Runnable r) {
            this.mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        SerialExecutor.this.scheduleNext();
                    }

                }
            });
            if(this.mActive == null) {
                this.scheduleNext();
            }

        }

        protected synchronized void scheduleNext() {
            if((this.mActive = (Runnable)this.mTasks.poll()) != null) {
                WorkTask.THREAD_POOL_EXECUTOR.execute(this.mActive);
            }

        }
    }

    private static class WorkThread extends Thread {
        static int threadCount;

        WorkThread(Runnable target, String name) {
            super(target, name);
            ++threadCount;
//            Logger.v("WorkTask", "WorkThread count = " + threadCount);
        }

        protected void finalize() throws Throwable {
            super.finalize();
            --threadCount;
//            Logger.v("WorkTask", "WorkThread count = " + threadCount);
        }
    }
}

