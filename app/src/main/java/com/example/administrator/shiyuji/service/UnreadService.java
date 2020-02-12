package com.example.administrator.shiyuji.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.service.message.MessageClient;
import com.example.administrator.shiyuji.service.notifier.UnreadCountNotifier;
import com.example.administrator.shiyuji.support.bean.UnreadCount;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.support.sqlit.SinaDB;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * 未读消息服务<br/>
 *
 * @author wangdan
 * 
 */
public class UnreadService extends Service {

	public static final String TAG = UnreadService.class.getSimpleName();
	public static final String ACTION_GET = "com.example.shiyuji.ACTION_GET";
	public static final String ACTION_UPDATE = "ACTION_UPDATE";
	public static final String ACTION_UNREAD_CHANGED = "ACTION_UNREAD_CHANGED";

	public static void startService() {
//		if (!AppSettings.isNotifyEnable())
//			return;
		
		Intent intent = new Intent(GlobalContext.getInstance(), UnreadService.class);
		intent.setAction(ACTION_GET);
		GlobalContext.getInstance().startService(intent);
	}
	
	public static void stopService() {

		if (AppContext.isLoggedIn()) {
			UnreadCountNotifier.mCount = new UnreadCount();
		}
		
		Intent intent = new Intent(GlobalContext.getInstance(), UnreadService.class);
		GlobalContext.getInstance().stopService(intent);
	}

	public static void updateAlarm() {
		Intent intent = new Intent(GlobalContext.getInstance(), UnreadService.class);
		intent.setAction(ACTION_UPDATE);
		GlobalContext.getInstance().startService(intent);
	}
	
	public UnreadCountNotifier unreadCountNotifier;


	@Override
	public void onCreate() {
		super.onCreate();

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (unreadCountNotifier == null)
			unreadCountNotifier = new UnreadCountNotifier(this);

		if (!AppContext.isLoggedIn()) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		// 未读消息重置
		if (AppContext.getAccount().getUnreadCount() == null) {
			AppContext.getAccount().setUnreadCount(UnreadService.getUnreadCount());
		}
		if (AppContext.getAccount().getUnreadCount() == null) {
			AppContext.getAccount().setUnreadCount(new UnreadCount());
		}

		//开启通知客户端
		try {
			Setting action = getSetting("base_url");
			String base_url = action.getValue().split("/")[2].split(":")[0];
			MessageClient client = new MessageClient(new URI("ws://"+base_url+":8887/"),AppContext.getAccount().getUserInfo(),unreadCountNotifier);
			client.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return super.onStartCommand(intent, flags, startId);
	}



	public static void sendUnreadBroadcast() {
		// 发出广播更新状态
		Intent intent = new Intent(ACTION_UNREAD_CHANGED);
		GlobalContext.getInstance().sendBroadcast(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static UnreadCount getUnreadCount() {
		if (!AppContext.isLoggedIn())
			return null;
		
		return SinaDB.getDB().selectById(null, UnreadCount.class, AppContext.getAccount().getUserInfo().getId());
	}

}
