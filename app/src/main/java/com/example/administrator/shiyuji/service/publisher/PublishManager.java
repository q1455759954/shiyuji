package com.example.administrator.shiyuji.service.publisher;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.service.PublishService;
import com.example.administrator.shiyuji.service.notifier.UnreadCountNotifier;
import com.example.administrator.shiyuji.support.bean.AccountBean;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.support.sqlit.PublishDB;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.AppointmentFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item.AppointmentDefFragment;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineDetailPagerFragment;
import com.example.administrator.shiyuji.ui.fragment.tuikit.util.TUikitUtil;
import com.example.administrator.shiyuji.util.accountutil.AccountUtils;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.common.utils.FileUtils;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/8/6.
 */

public class PublishManager extends Handler implements PublishQueue.PublishQueueCallback {

    public static final int publishDelay = 1;

    private Context context;

    private PublishQueue publishQueue;
//    private PublishNotifier publishNotifier;

    private PublishTask publishTask;

    private AccountBean mAccount;
    private UserInfo loggedIn;

    public PublishManager(Context context, AccountBean accountBean) {
        this.context = context.getApplicationContext();
        this.mAccount = accountBean;
        this.loggedIn = accountBean.getUserInfo();
        publishQueue = new PublishQueue(this);
//        publishNotifier = new PublishNotifier(context);

        publishInit();
    }
    /**
     * 将添加状态的消息都加入到队列当中
     */
    public void publishInit() {
        List<PublishBean> beans = PublishDB.getPublishOfAddStatus(loggedIn);
        for (PublishBean bean : beans)
            publishQueue.add(bean);

        onPublish(publishQueue.peek());
    }

    /**
     * 取消当前发送的，如果队列里还有其它的接着发送
     */
    public void cancelPublish() {
        //取消延迟发送？
        removeMessages(publishDelay);
        //删除队头元素
        PublishBean bean = publishQueue.poll();
        if (bean != null) {
            //存草稿
//            bean.setStatus(PublishStatus.draft);
            PublishDB.addPublish(bean, loggedIn);

//            publishNotifier.notifyPublishCancelled(bean);

//            refreshDraftbox();
            //发送下一个
            onPublish(publishQueue.peek());
        }
    }

    public void stop() {
        if (publishTask != null && publishTask.getStatus() != WorkTask.Status.FINISHED)
            publishTask.cancel(true);

        removeMessages(publishDelay);
//        if (!AppContext.isLoggedIn() || !AppContext.getAccount().getUser().getIdstr().equals(loggedIn.getIdstr()))
//            PublishNotifier.cancelAll();

        if (publishQueue.size() > 0) {
            new WorkTask<UserInfo, Void, Void>() {

                @Override
                public Void workInBackground(UserInfo... params) throws TaskException {
                    PublishBean bean = null;
//                    Logger.d(AccountFragment.TAG, String.format("共有%d个发布任务未完成", publishQueue.size()));
                    while((bean = publishQueue.poll()) != null) {
//                        Logger.d(AccountFragment.TAG, "停止发布一个任务，添加到草稿");

//                        bean.setStatus(PublishStatus.draft);

                        PublishDB.addPublish(bean, params[0]);
                    }
                    return null;
                }

            }.execute(loggedIn);
        }
    }

    public void onPublish(PublishBean bean) {
        if (bean == null)
            return;

        // 如果队列为空，放入首位等待发送，否则，添加到队列等待发布
//        if (publishQueue.isEmpty()) {
//            publishQueue.add(bean);
//        }
//
//        PublishBean firstBean = publishQueue.peek();

        if (bean.getPublishType()== PublishBean.PublishType.appointment){
            //如果是约信息，先建立群组，然后通过回调上传到服务器
            //建立群组
            TUikitUtil.createGroup(this,bean);
        }else {
            publishTask = new PublishTask(bean);
            publishTask.executeOnSerialExecutor();
        }

//        refreshDraftbox();
    }

    public void parseAppointment(boolean result, PublishBean bean, String id){
        if (result){
            bean.getAppointmentInfo().setGroupId(id);
            publishTask = new PublishTask(bean);
            publishTask.executeOnSerialExecutor();
        }else{
            Toast.makeText(context,"创建失败，未知原因",Toast.LENGTH_SHORT);
        }
    }

    /**
     * 发送完成后，结束service
     * @param bean
     */
    private synchronized void publishFinished(PublishBean bean) {
        publishQueue.poll();

//        Logger.w("publishFinished" + publishQueue.size());

        // 队列发送完毕了，且当前运行的页面不是发布页面，就停止服务
        if (publishQueue.size() == 0)
            context.stopService(new Intent(context, PublishService.class));
        else {
            postDelayed(new Runnable() {

                @Override
                public void run() {
                    onPublish(publishQueue.peek());
                }

            }, 2 * 1000);
        }
    }

    @Override
    public void onPublishPoll(PublishBean bean) {

    }

    @Override
    public void onPublishAdd(PublishBean bean) {

    }

    @Override
    public void onPublishPeek(PublishBean bean) {

    }

    class PublishTask extends WorkTask<Void, Void, Object> {

        PublishBean bean;

        public PublishTask(PublishBean bean) {
            this.bean = bean;
        }

        @Override
        public Object workInBackground(Void... var1) throws TaskException {
            if (bean == null)
                return null;
            //是发布约信息
            if (bean.getPublishType() == PublishBean.PublishType.appointment) {
                return SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).publishAppointment(bean.getAppointmentInfo());
            } else if (bean.getPublishType() == PublishBean.PublishType.commentCreate) {
                //创建评论
                return SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).publishComments(bean.getStatusComment());
            } else if (bean.getPublishType() == PublishBean.PublishType.status) {
                //创建动态
                //先判断是否含有图片
                if (bean.getPics() != null && bean.getPics().length > 0) {
                    String[] images = bean.getPics();
                    PicUrls[] pic_urls = new PicUrls[images.length];

                    for (int i = 0; i < images.length; i++) {
                        String path = images[i];
                        if (path.toString().startsWith("content://")) {
                            Uri uri = Uri.parse(path);
                            path = FileUtils.getPath(GlobalContext.getInstance(), uri);
                        } else {
                            path = path.toString().replace("file://", "");
                        }
                        File file = new File(path);

                        if (!file.exists())
                            throw new TaskException("图片不存在或已删除");

                        // 压缩文件
                        file = AisenUtils.getUploadFile(context, file);

                        // 开始上传
                        String result = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).uploadPicture(file);
                        PicUrls picUrls = new PicUrls();
                        picUrls.setThumbnail_pic(result);
                        pic_urls[i] = picUrls;
                    }
                    bean.getStatusContent().setPic_urls(pic_urls);
                }
                //发布
                return SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).publishStatus(bean.getStatusContent());


            } else if (bean.getPublishType() == PublishBean.PublishType.life) {
                //先判断是否含有图片
                if (bean.getPics() != null && bean.getPics().length > 0) {
                    String[] images = bean.getPics();

                    PicUrls[] pic_urls = new PicUrls[images.length];
                    for (int i = 0; i < images.length; i++) {
                        String path = images[i];
                        if (path.toString().startsWith("content://")) {
                            Uri uri = Uri.parse(path);
                            path = FileUtils.getPath(GlobalContext.getInstance(), uri);
                        } else {
                            path = path.toString().replace("file://", "");
                        }
                        File file = new File(path);

                        if (!file.exists())
                            throw new TaskException("图片不存在或已删除");

                        // 压缩文件
                        file = AisenUtils.getUploadFile(context, file);

                        // 开始上传
                        String result = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).uploadPicture(file);
                        PicUrls picUrls = new PicUrls();
                        picUrls.setThumbnail_pic(result);
                        pic_urls[i] = picUrls;
                    }
                    bean.getLifeInfo().getCommodity().setPicUrls(pic_urls);
                }
                //发布
                return SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).publishLifeInfo(bean.getLifeInfo());

            }
            return null;

        }
        @Override
        protected void onSuccess(Object result) {
            super.onSuccess(result);
            // 回复微博、评论和转发微博时，通知刷新界面
            if (bean != null) {
                if (bean.getPublishType() == PublishBean.PublishType.appointment) {
                    //发出通知
                    switch (bean.getAppointmentInfo().getType()){
                        case "game":
                            GlobalContext.getInstance().sendBroadcast(new Intent(AppointmentFragment.ACTION_REFRESH_CMT_GAME));
                            break;
                        case "study":
                            GlobalContext.getInstance().sendBroadcast(new Intent(AppointmentFragment.ACTION_REFRESH_CMT_STUDY));
                            break;
                        case "exercise":
                            GlobalContext.getInstance().sendBroadcast(new Intent(AppointmentFragment.ACTION_REFRESH_CMT_EXERCISE));
                            break;
                        case "play":
                            GlobalContext.getInstance().sendBroadcast(new Intent(AppointmentFragment.ACTION_REFRESH_CMT_PLAY));
                            break;
                    }
                }else if (bean.getPublishType()== PublishBean.PublishType.status){
                    //发布成功通知
                    StatusContent statusContent = (StatusContent) result;
                    AccountBean accountBean = AppContext.getAccount();
                    accountBean.setUserInfo(statusContent.getUserInfo());
                    AccountUtils.setLogedinAccount(accountBean);
                    AppContext.setAccount(accountBean);//保存当前账户
                    Toast.makeText(context,"发布成功",Toast.LENGTH_SHORT).show();
                }
//                else if (bean.getType() == PublishType.commentReply) {
//                    GlobalContext.getInstance().sendBroadcast(new Intent(TimelineDetailPagerFragment.ACTION_REFRESH_CMT_REPLY));
//                }
//                else if (bean.getType() == PublishType.statusRepost) {
//                    GlobalContext.getInstance().sendBroadcast(new Intent(TimelineDetailPagerFragment.ACTION_REFRESH_REPOST));
//                }
//
//                publishNotifier.notifyPublishSuccess(bean);
//
//                PublishDB.deletePublish(bean, loggedIn);
            }
        }
        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);
            if (bean != null) {
                // 响应成功，就让他发成功
//                if (TaskException.TaskError.socketTimeout.toString().equalsIgnoreCase(exception.getCode())) {
//                    onSuccess(null);
//                }
//                else {
//                    publishNotifier.notifyPublishFaild(bean, exception.getMessage());
//
//                    bean.setStatus(PublishStatus.faild);
//                    SinaErrorMsgUtil util = new SinaErrorMsgUtil();
//                    if (util.checkCode(exception.getCode()) != null)
//                        bean.setErrorMsg(util.checkCode(exception.getCode()));
//                    else
//                        bean.setErrorMsg(exception.getMessage());
//                    PublishDB.updatePublish(bean, loggedIn);
//
////                    refreshDraftbox();
//                }
            }
        }

        @Override
        protected void onFinished() {
            super.onFinished();
            if (bean != null){
                publishFinished(bean);

//                refreshDraftbox();
            }
        }



    }

}
