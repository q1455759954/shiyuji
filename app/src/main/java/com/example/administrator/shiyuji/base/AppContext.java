package com.example.administrator.shiyuji.base;

import android.util.Log;

import com.example.administrator.shiyuji.service.UnreadService;
import com.example.administrator.shiyuji.service.message.MessageClient;
import com.example.administrator.shiyuji.service.notifier.UnreadCountNotifier;
import com.example.administrator.shiyuji.support.bean.AccountBean;
import com.example.administrator.shiyuji.support.bean.UnreadCount;
import com.example.administrator.shiyuji.util.accountutil.AccountUtils;
import com.example.administrator.shiyuji.util.common.GlobalContext;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Administrator on 2019/8/6.
 */

public class AppContext {

    private static AccountBean mAccount;

    public static boolean isLoggedIn() {
        return mAccount != null && mAccount.getAccessToken() != null;
    }

    public static void login(AccountBean account) {
        //重新设置创建时间，token过期时间就会自动延长（过期是指多长时间不登录）
        account.getAccessToken().setCreate_at(System.currentTimeMillis());
        AccountUtils.setLogedinAccount(account);
        AppContext.setAccount(account);//保存当前账户

        //开启服务
        UnreadService.startService();


    }

    public static void setAccount(AccountBean account) {
    mAccount = account;
}

    public static AccountBean getAccount() {
        return mAccount;
    }

}
