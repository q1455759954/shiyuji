package com.example.administrator.shiyuji.service.notifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.administrator.shiyuji.util.common.GlobalContext;


public abstract class Notifier {

	final protected Context context;
	final private NotificationManager notificationManager;

	public static final long[] vibrate = new long[]{ 0, 150, 100, 250 };

	public static final int RemindUnreadLikeNum = 0000;// 点赞

	public static final int PublishCommentNotificationRequest = 1000;// 回复微博

	public static final int PublishStatusNotificationRequest = 2000;// 新微博

	public static final int ReplyCommentNotificationRequest = 3000;// 回复评论
	
	public static final int RepostStatusNotificationRequest = 7000;// 转发微博

	public static final int RemindUnreadComments = 9000;// 新评论

	public static final int RemindUnreadForMentionComments = 4000;// 新提及评论

	public static final int RemindUnreadForMentionStatus = 5000;// 新提及微博
	
	public static final int RemindUnreadForDM = 8000;// 新私信

	public static final int RemindUnreadForFollowers = 6000;// 新粉丝

    public static final int OfflineStatus = 9001;// 离线微博

    public static final int OfflineCmt = 9002;// 离线评论

    public static final int OfflinePicture = 9003;// 离线图片

	public Notifier(Context context) {
		this.context = context;
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}


	public NotificationManager getNotificationManager(){
		return this.notificationManager;
	}

	final public void cancelNotification(int request) {
		notificationManager.cancel(request);
	}
	
	final public static void cancelAll() {
		NotificationManager notificationManager = (NotificationManager) GlobalContext.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.cancel(PublishCommentNotificationRequest);
		notificationManager.cancel(PublishStatusNotificationRequest);
		notificationManager.cancel(ReplyCommentNotificationRequest);
		notificationManager.cancel(RepostStatusNotificationRequest);
		notificationManager.cancel(RemindUnreadComments);
		notificationManager.cancel(RemindUnreadForMentionComments);
		notificationManager.cancel(RemindUnreadForMentionStatus);
		notificationManager.cancel(RemindUnreadForFollowers);
		notificationManager.cancel(RemindUnreadForDM);
	}
	
}
