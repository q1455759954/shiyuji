package com.example.administrator.shiyuji.ui.widget.support.bitmap;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2019/7/2.
 */

public abstract class BitmapTask<Params, Progress, Result> {
    static final String TAG = "BitmapTask";
    private static final int CORE_IMAGE_POOL_SIZE = 20;
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 256;
    private static final int KEEP_ALIVE = 1;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "BitmapTask #" + this.mCount.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(10);
    public static final Executor THREAD_POOL_EXECUTOR;
    public static final Executor IMAGE_POOL_EXECUTOR;
    public static final Executor SERIAL_EXECUTOR;
    private static final int MESSAGE_POST_RESULT = 1;
    private static final int MESSAGE_POST_PROGRESS = 2;
    private static final BitmapTask.InternalHandler sHandler;
    private static volatile Executor sDefaultExecutor;
    private final BitmapTask.WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;
    private volatile BitmapTask.Status mStatus;
    private Exception exception;
    private final AtomicBoolean mTaskInvoked;
    static int count;

    public static void init() {
        sHandler.getLooper();
    }

    private static void setDefaultExecutor(Executor exec) {
        sDefaultExecutor = exec;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        --count;
    }

    public BitmapTask() {
        this.mStatus = BitmapTask.Status.PENDING;
        this.mTaskInvoked = new AtomicBoolean();
        ++count;
//        Logger.d("BitmapTask", count + "");
        this.mWorker = new BitmapTask.WorkerRunnable() {
            public Result call() throws Exception {
                BitmapTask.this.mTaskInvoked.set(true);
                Process.setThreadPriority(10);
                return BitmapTask.this.postResult(BitmapTask.this.doInBackground((Params[]) this.mParams));
            }
        };
        this.mFuture = new FutureTask(this.mWorker) {
            protected void done() {
                try {
                    Object t = this.get();
                    BitmapTask.this.postResultIfNotInvoked((Result) t);
                } catch (InterruptedException var2) {
                    Log.w("BitmapTask", var2);
                } catch (ExecutionException var3) {
                    throw new RuntimeException("An error occured while executing doInBackground()", var3.getCause());
                } catch (CancellationException var4) {
                    BitmapTask.this.postResultIfNotInvoked((Result) null);
                } catch (Throwable var5) {
                    throw new RuntimeException("An error occured while executing doInBackground()", var5);
                }

            }
        };
    }

    private void postResultIfNotInvoked(Result result) {
        boolean wasTaskInvoked = this.mTaskInvoked.get();
        if(!wasTaskInvoked) {
            this.postResult(result);
        }

    }

    private Result postResult(Result result) {
        Message message = sHandler.obtainMessage(1, new BitmapTask.AsyncTaskResult(this, new Object[]{result}));
        message.sendToTarget();
        return result;
    }

    public final BitmapTask.Status getStatus() {
        return this.mStatus;
    }

    protected void onTaskStarted() {
    }

    protected void onTaskFailed(Exception exception) {
    }

    protected void onTaskSuccess(Result result) {
    }

    protected void resultIsNull() {
    }

    protected void onTaskComplete() {
    }

    public abstract Result workInBackground(Params... var1) throws Exception;

    private Result doInBackground(Params... params) {
//        Logger.d("BitmapTask", String.format("%s --->doInBackground()", new Object[]{"run "}));run

        try {
            return this.workInBackground(params);
        } catch (Exception var3) {
            var3.printStackTrace();
            this.exception = var3;
            return null;
        }
    }

    protected final void onPreExecute() {
        this.onTaskStarted();
    }

    protected final void onPostExecute(Result result) {
        if(this.exception == null) {
            if(result == null) {
                this.resultIsNull();
            } else {
                this.onTaskSuccess(result);
            }
        } else if(this.exception != null) {
            this.onTaskFailed(this.exception);
        }

        this.onTaskComplete();
    }

    protected void onProgressUpdate(Progress... values) {
    }

    protected void onCancelled(Result result) {
        this.onCancelled();
//        Logger.d("BitmapTask", "onCanceled()");
    }

    protected void onCancelled() {
        this.onTaskComplete();
    }

    public final boolean isCancelled() {
        return this.mFuture.isCancelled();
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        return this.mFuture.cancel(mayInterruptIfRunning);
    }

    public final Result get() throws InterruptedException, ExecutionException {
        return this.mFuture.get();
    }

    public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.mFuture.get(timeout, unit);
    }

    public final BitmapTask<Params, Progress, Result> executeOnSerialExecutor(Params... params) {
        return this.executeOnExecutor(SERIAL_EXECUTOR, params);
    }

    public final BitmapTask<Params, Progress, Result> executrOnImageExecutor(Params... params) {
        return this.executeOnExecutor(IMAGE_POOL_EXECUTOR, params);
    }

    public final BitmapTask<Params, Progress, Result> execute(Params... params) {
        return this.executeOnExecutor(THREAD_POOL_EXECUTOR, params);
    }

    private final BitmapTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
//        if(this.mStatus != BitmapTask.Status.PENDING) {
//            switch(null.$SwitchMap$org$aisen$android$component$bitmaploader$core$BitmapTask$Status[this.mStatus.ordinal()]) {
//                case 1:
//                    throw new IllegalStateException("Cannot execute task: the task is already running.");
//                case 2:
//                    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
//            }
//        }

        this.mStatus = BitmapTask.Status.RUNNING;
        this.onPreExecute();
        this.mWorker.mParams = params;
        exec.execute(this.mFuture);
        return this;
    }

    public static void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }

    protected final void publishProgress(Progress... values) {
        if(!this.isCancelled()) {
            sHandler.obtainMessage(2, new BitmapTask.AsyncTaskResult(this, values)).sendToTarget();
        }

    }

    private void finish(Result result) {
        if(this.isCancelled()) {
            this.onCancelled(result);
        } else {
            this.onPostExecute(result);
        }

        this.mStatus = BitmapTask.Status.FINISHED;
    }

    static {
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(10, 256, 1L, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        IMAGE_POOL_EXECUTOR = Executors.newFixedThreadPool(20, sThreadFactory);
        SERIAL_EXECUTOR = new BitmapTask.SerialExecutor();
        sHandler = new BitmapTask.InternalHandler();
        sDefaultExecutor = SERIAL_EXECUTOR;
        count = 0;
    }

    private static class AsyncTaskResult<Data> {
        final BitmapTask mTask;
        final Data[] mData;

        AsyncTaskResult(BitmapTask task, Data... data) {
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
        private InternalHandler() {
        }

        public void handleMessage(Message msg) {
            BitmapTask.AsyncTaskResult result = (BitmapTask.AsyncTaskResult)msg.obj;
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
                BitmapTask.THREAD_POOL_EXECUTOR.execute(this.mActive);
            }

        }
    }
}
