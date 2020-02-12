package com.example.administrator.shiyuji.util.accountutil;

import com.alibaba.fastjson.JSON;
import com.example.administrator.shiyuji.support.bean.AccountBean;
import com.example.administrator.shiyuji.support.sqlit.SinaDB;

import java.util.List;

/**
 * Created by Administrator on 2019/9/3.
 */


public class AccountUtils {

    public static void newAccount(AccountBean account) {
        SinaDB.getDB().insertOrReplace(null, account);
    }

//    public static void updateAccount(AccountBean account) {
//        SinaDB.getDB().update(null, account);
//    }

    public static List<AccountBean> queryAccount() {
        return SinaDB.getDB().select(null, AccountBean.class);
    }

//    public static void remove(String id) {
//        SinaDB.getDB().deleteById(null, AccountBean.class, id);
//    }

    public static void setLogedinAccount(AccountBean account) {
        SinaDB.getDB().deleteAll(null, AccountBean.class);

        SinaDB.getDB().insert(null, account);
    }

    public static AccountBean getLogedinAccount() {
        List<AccountBean> accounts =  SinaDB.getDB().select(null, AccountBean.class);
        if (accounts.size() > 0)
            return accounts.get(0);

        return null;
    }

}
