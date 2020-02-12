package com.example.administrator.shiyuji.service.notifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;


import com.example.administrator.shiyuji.MainActivity;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.support.bean.AccountBean;
import com.example.administrator.shiyuji.support.bean.UnreadCount;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.support.sqlit.SinaDB;
import com.example.administrator.shiyuji.util.accountutil.AccountUtils;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.task.TaskException;

public class UnreadCountNotifier extends Notifier {

	public UnreadCountNotifier(Context context) {
		super(context);
	}
	
	// 保存最后一次的数据，跟上一次匹配，当数据不同时且大于0时，发出通知提醒用户
	public static UnreadCount mCount = new UnreadCount();

	public void notifyPublishSuccess(){
		Notification notification = getNotification(RemindUnreadForFollowers,"发布","成功",R.mipmap.ic_launcher,MainActivity.ACTION_NOTIFICATION_FANS);
		notify(RemindUnreadForFollowers, notification);
	}

	public void notinfyUnreadCount(UnreadCount count) {
		String fromAisen = context.getString(R.string.notifer_from_aisen);

		// 新粉丝
		if (count.getFans() != 0) {
			//获取用户信息 刷新界面
			AccountBean accountBean = AppContext.getAccount();
			try {
				accountBean.setUserInfo(SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).getUserInfo(String.valueOf(accountBean.getUserInfo().getId())));
				AccountUtils.setLogedinAccount(accountBean);

			} catch (TaskException e) {
				e.printStackTrace();
			}


			if (count.getFans()>0){

				AppContext.getAccount().getUnreadCount().setFans(AppContext.getAccount().getUnreadCount().getFans()+count.getFans());
				String contentTitle = String.format(context.getString(R.string.notifer_new_followers), AppContext.getAccount().getUnreadCount().getFans());

				Notification notification = getNotification(RemindUnreadForFollowers,"新粉丝",contentTitle,R.mipmap.ic_launcher,MainActivity.ACTION_NOTIFICATION_FANS);

				notify(RemindUnreadForFollowers, notification);
			}

		}

		// 新评论
		if (count.getComment_num() != 0) {
			AppContext.getAccount().getUnreadCount().setComment_num(AppContext.getAccount().getUnreadCount().getComment_num()+count.getComment_num());
			String contentTitle = String.format(context.getString(R.string.notifer_new_cmts), AppContext.getAccount().getUnreadCount().getComment_num());

			Notification notification = getNotification(RemindUnreadComments,"新评论",contentTitle,R.mipmap.ic_launcher, MainActivity.ACTION_NOTIFICATION_LIKE);

			notify(RemindUnreadComments, notification);

		}

		// 新点赞
		if (count.getLike_num() != 0) {
			AppContext.getAccount().getUnreadCount().setLike_num(AppContext.getAccount().getUnreadCount().getLike_num()+count.getLike_num());
			String contentTitle = String.format(context.getString(R.string.notifer_new_like), AppContext.getAccount().getUnreadCount().getLike_num());

			Notification notification = getNotification(RemindUnreadLikeNum,"新点赞",contentTitle,R.mipmap.ic_launcher, MainActivity.ACTION_NOTIFICATION_LIKE);

			notify(RemindUnreadLikeNum, notification);

		}

		//保存到数据库
		AppContext.getAccount().getUnreadCount().setId(String.valueOf(AppContext.getAccount().getUserInfo().getId()));
		LikeDB.update(AppContext.getAccount().getUnreadCount());


//		// 新提及我的微博数
//		if (count.getMention_status() != 0 && mCount.getMention_status() != count.getMention_status()) {
//			String contentTitle = String.format(context.getString(R.string.notifer_new_mention_status), count.getMention_status());
//
//			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//			builder.setSmallIcon(R.drawable.statusbar_ic_mention_small).setContentTitle(contentTitle)
//					.setContentText(fromAisen);
//			builder.setTicker(contentTitle);
//
//			Intent intent = new Intent(context, AppointmentFragment.class);
//            intent.setAction(AppointmentFragment.ACTION_NOTIFICATION_MS);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadForMentionStatus, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//			builder.setContentIntent(contentIntent).setAutoCancel(true);
//
//			notify(RemindUnreadForMentionStatus, builder);
//		}
//		mCount.setMention_status(count.getMention_status());
//		if (count.getMention_status() == 0)
//			cancelNotification(RemindUnreadForMentionStatus);
//
//		// 新提及我的评论数
//		if (count.getMention_cmt() != 0 && mCount.getMention_cmt() != count.getMention_cmt()) {
//			String contentTitle = String.format(context.getString(R.string.notifer_new_mention_cmt), count.getMention_cmt());
//
//			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//			builder.setSmallIcon(R.drawable.statusbar_ic_mention_small).setContentTitle(contentTitle)
//					.setContentText(fromAisen);
//			builder.setTicker(contentTitle);
//
//			Intent intent = new Intent(context, AppointmentFragment.class);
//            intent.setAction(AppointmentFragment.ACTION_NOTIFICATION_MC);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadForMentionComments, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//			builder.setContentIntent(contentIntent).setAutoCancel(true);
//
//			notify(RemindUnreadForMentionComments, builder);
//		}
//		mCount.setMention_cmt(count.getMention_cmt());
//		if (count.getMention_cmt() == 0)
//			cancelNotification(RemindUnreadForMentionComments);
//
////		Logger.e(mCount);
//		// 新私信
//		if (count.getDm() != 0 && mCount.getDm() != count.getDm()) {
//			String contentTitle = String.format(context.getString(R.string.notifer_new_dm), count.getDm());
//
//			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//			builder.setSmallIcon(R.drawable.statusbar_ic_dm_small).setContentTitle(contentTitle)
//					.setContentText(fromAisen);
//			builder.setTicker(contentTitle);
//
//			Intent intent = new Intent(context, AppointmentFragment.class);
//            intent.setAction(AppointmentFragment.ACTION_NOTIFICATION);
//            intent.putExtra("type", "10");
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadForDM, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//			builder.setContentIntent(contentIntent).setAutoCancel(true);
//
//			notify(RemindUnreadForDM, builder);
//		}
//		mCount.setDm(count.getDm());
//		if (count.getDm() == 0)
//			cancelNotification(RemindUnreadForDM);

	}

	private Notification getNotification(int id, String title, String contentTitle, int icon, String action){
		Notification notification = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel mChannel = new NotificationChannel(String.valueOf(id), title, NotificationManager.IMPORTANCE_LOW);
			getNotificationManager().createNotificationChannel(mChannel);
			notification = new Notification.Builder(context)
					.setChannelId(String.valueOf(id))
					.setContentTitle(title)
					.setContentText(contentTitle)
					.setSmallIcon(R.mipmap.ic_launcher).build();
		} else {
			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
					.setContentTitle(title)
					.setContentText(contentTitle)
					.setSmallIcon(R.mipmap.ic_launcher)
					.setAutoCancel(true)
					.setWhen(System.currentTimeMillis())
					.setOngoing(true);
//					.setChannel(id);//无效
			Intent intent = new Intent(context, MainActivity.class);
			intent.setAction(action);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			notificationBuilder.setContentIntent(contentIntent).setAutoCancel(true);
			notification = notificationBuilder.build();
		}
		return notification;
	}
	
	private void notify(int request, Notification notification) {

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notification.flags = notification.defaults;
		if (AppSettings.isNotifyVibrate()) {
			notification.defaults = 0;
			notification.vibrate = vibrate;
		}
		if (AppSettings.isNotifyLED()) {
//			notification.defaults = Notification.DEFAULT_LIGHTS;
			notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
//			notification.ledARGB = Color.parseColor(AppSettings.getThemeColor());
			notification.ledARGB = Color.BLUE;
			notification.ledOffMS = 200;
			notification.ledOnMS = 700;
		}
		if (AppSettings.isNotifySound()) {
			notification.defaults = notification.defaults | Notification.DEFAULT_SOUND;
		}

		getNotificationManager().notify(request, notification);

	}
	

}
