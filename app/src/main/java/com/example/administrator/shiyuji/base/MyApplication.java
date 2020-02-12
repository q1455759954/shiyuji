package com.example.administrator.shiyuji.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Html;
import android.util.Log;

import com.example.administrator.shiyuji.service.message.MessageClient;
import com.example.administrator.shiyuji.support.config.ConfigHelper;
import com.example.administrator.shiyuji.support.genereter.GenerateTestUserSig;
import com.example.administrator.shiyuji.ui.widget.MyTextView;
import com.example.administrator.shiyuji.util.accountutil.AccountUtils;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.support.setting.SettingUtility;
import com.example.administrator.shiyuji.support.sqlit.EmotionsDB;
import com.example.administrator.shiyuji.support.sqlit.SinaDB;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.tencent.qcloud.tim.uikit.TUIKit;


import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Administrator on 2019/7/2.
 */

public class MyApplication extends GlobalContext {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化tuikit
        TUIKit.init(getApplicationContext(), GenerateTestUserSig.SDKAPPID, new ConfigHelper().getConfigs());

        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
//        NIMClient.init(this, loginInfo(), options());

//        // 添加一些配置项
        SettingUtility.addSettings(this, "actions");
        SettingUtility.addSettings(this, "settings");
//        // 初始化一个颜色主题
//        setupTheme();
//        // 打开Debug日志
//
//        setupCrash();
        // 初始化图片加载
        BitmapLoader.newInstance(this, getImagePath());

        // 配置异常处理类

        // 初始化数据库
        SinaDB.setInitDB();
        // 检查表情
        try {
            EmotionsDB.checkEmotions();
        } catch (Exception e) {
        }
        // 设置登录账号
        AppContext.setAccount(AccountUtils.getLogedinAccount());
        if (AppContext.isLoggedIn()){
            AppContext.login(AppContext.getAccount());
        }

    }



    public static String getImagePath() {
        return GlobalContext.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator;
    }


//    // 如果返回值为 null，则全部使用默认参数。
//    private SDKOptions options() {
//        MixPushConfig mixPushConfig = new MixPushConfig();
//        mixPushConfig.hwCertificateName = "C1:DE:25:DB:F4:11:BB:74:15:37:E4:CD:5F:B4:51:EE:DA:F9:82:FC:B0:18:67:EB:88:CC:2C:93:5C:1C:6E:F1";
//        SDKOptions options = new SDKOptions();
//
//        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
//        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
////        config.notificationEntrance = TabHostActivity.class; // 点击通知栏跳转到该Activity
////        config.notificationSmallIconId = R.drawable.logo;
//        // 呼吸灯配置
//        config.ledARGB = Color.GREEN;
//        config.ledOnMs = 1000;
//        config.ledOffMs = 1500;
//        // 通知铃声的uri字符串
//        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
//        options.statusBarNotificationConfig = config;
//        options.mixPushConfig = mixPushConfig;
//
//        // 配置保存图片，文件，log 等数据的目录
//        // 如果 options 中没有设置这个值，SDK 会使用采用默认路径作为 SDK 的数据目录。
//        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
//        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim"; // 可以不设置，那么将采用默认路径
//        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
//        options.sdkStorageRootPath = sdkPath;
//
//        // 配置是否需要预下载附件缩略图，默认为 true
//        options.preloadAttach = true;
//
//        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
//        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
//        options.thumbnailSize = 480 / 2;
//        options.userInfoProvider = new UserInfoProvider() {
//
//            @Override
//            public UserInfo getUserInfo(String account) {
//                return null;
//            }
//
//            @Override
//            public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
//                return null;
//            }
//
//            @Override
//            public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
//                return null;
//            }
//        };
//
//        return options;
//    }
//    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
//    private LoginInfo loginInfo() {
//        return null;
//    }

}

