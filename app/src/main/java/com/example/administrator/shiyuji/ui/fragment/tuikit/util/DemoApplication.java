//package com.example.administrator.shiyuji.ui.fragment.tuikit.util;
//
//import android.app.Activity;
//import android.app.Application;
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.example.administrator.shiyuji.support.config.ConfigHelper;
//import com.example.administrator.shiyuji.support.genereter.GenerateTestUserSig;
//import com.tencent.imsdk.TIMManager;
//import com.tencent.imsdk.session.SessionWrapper;
//import com.tencent.qcloud.tim.uikit.TUIKit;
//
//import java.util.List;
//
///**
// * Created by Administrator on 2019/9/6.
// */
//
//
//public class DemoApplication extends Application {
//
//    private static final String TAG = DemoApplication.class.getSimpleName();
//
//    private static DemoApplication instance;
//
//    @Override
//    public void onCreate() {
//
//        super.onCreate();
//        MultiDex.install(this);
//        // bugly上报
////        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
////        strategy.setAppVersion(TIMManager.getInstance().getVersion());
////        CrashReport.initCrashReport(getApplicationContext(), PrivateConstants.BUGLY_APPID, true, strategy);
//
//        //判断是否是在主线程
//        if (SessionWrapper.isMainProcess(getApplicationContext())) {
//            /**
//             * TUIKit的初始化函数
//             *
//             * @param context  应用的上下文，一般为对应应用的ApplicationContext
//             * @param sdkAppID 您在腾讯云注册应用时分配的sdkAppID
//             * @param configs  TUIKit的相关配置项，一般使用默认即可，需特殊配置参考API文档
//             */
//            TUIKit.init(this, GenerateTestUserSig.SDKAPPID, new ConfigHelper().getConfigs());
//
//            if ( ThirdPushTokenMgr.USER_GOOGLE_FCM ) {
//                FirebaseInstanceId.getInstance().getInstanceId()
//                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                            @Override
//                            public void onComplete(Task<InstanceIdResult> task) {
//                                if (!task.isSuccessful()) {
//                                    DemoLog.w(TAG, "getInstanceId failed exception = " + task.getException());
//                                    return;
//                                }
//
//                                // Get new Instance ID token
//                                String token = task.getResult().getToken();
//                                DemoLog.i(TAG, "google fcm getToken = " + token);
//
//                                ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
//                            }
//                        });
//            } else if (IMFunc.isBrandXiaoMi()) {
//                // 小米离线推送
//                MiPushClient.registerPush(this, PrivateConstants.XM_PUSH_APPID, PrivateConstants.XM_PUSH_APPKEY);
//            } else if (IMFunc.isBrandHuawei()) {
//                // 华为离线推送
//                HMSAgent.init(this);
//            } else if (MzSystemUtils.isBrandMeizu(this)) {
//                // 魅族离线推送
//                PushManager.register(this, PrivateConstants.MZ_PUSH_APPID, PrivateConstants.MZ_PUSH_APPKEY);
//            } else if (IMFunc.isBrandVivo()) {
//                // vivo离线推送
//                PushClient.getInstance(getApplicationContext()).initialize();
//            }
//
//            registerActivityLifecycleCallbacks(new StatisticActivityLifecycleCallback());
//        }
//        instance = this;
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
//    }
//
//    class StatisticActivityLifecycleCallback implements ActivityLifecycleCallbacks {
//        private int foregroundActivities = 0;
//        private boolean isChangingConfiguration;
//
//        @Override
//        public void onActivityCreated(Activity activity, Bundle bundle) {
//            DemoLog.i(TAG, "onActivityCreated bundle: " + bundle);
//            if (bundle != null) { // 若bundle不为空则程序异常结束
//                // 重启整个程序
//                Intent intent = new Intent(activity, SplashActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//                startActivity(intent);
//            }
//        }
//
//        @Override
//        public void onActivityStarted(Activity activity) {
//            foregroundActivities++;
//            if (foregroundActivities == 1 && !isChangingConfiguration) {
//                // 应用切到前台
//                DemoLog.i(TAG, "application enter foreground");
//                TIMManager.getInstance().doForeground(new TIMCallBack() {
//                    @Override
//                    public void onError(int code, String desc) {
//                        DemoLog.e(TAG, "doForeground err = " + code + ", desc = " + desc);
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        DemoLog.i(TAG, "doForeground success");
//                    }
//                });
//            }
//            isChangingConfiguration = false;
//        }
//
//        @Override
//        public void onActivityResumed(Activity activity) {
//
//        }
//
//        @Override
//        public void onActivityPaused(Activity activity) {
//
//        }
//
//        @Override
//        public void onActivityStopped(Activity activity) {
//            foregroundActivities--;
//            if (foregroundActivities == 0) {
//                // 应用切到后台
//                DemoLog.i(TAG, "application enter background");
//                int unReadCount = 0;
//                List<TIMConversation> conversationList = TIMManager.getInstance().getConversationList();
//                for (TIMConversation timConversation : conversationList) {
//                    unReadCount += timConversation.getUnreadMessageNum();
//                }
//                TIMBackgroundParam param = new TIMBackgroundParam();
//                param.setC2cUnread(unReadCount);
//                TIMManager.getInstance().doBackground(param, new TIMCallBack() {
//                    @Override
//                    public void onError(int code, String desc) {
//                        DemoLog.e(TAG, "doBackground err = " + code + ", desc = " + desc);
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        DemoLog.i(TAG, "doBackground success");
//                    }
//                });
//            }
//            isChangingConfiguration = activity.isChangingConfigurations();
//        }
//
//        @Override
//        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onActivityDestroyed(Activity activity) {
//
//        }
//    }
//
//    public static DemoApplication instance() {
//        return instance;
//    }
//
//}
